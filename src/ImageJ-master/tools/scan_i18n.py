# -*- coding: utf-8 -*-
"""扫描源码中面向用户的 UI 字符串，列出未被词典覆盖的条目。"""
import io, os, re, sys, json

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DICT = os.path.join(ROOT, 'ij', 'i18n', 'IJ_Lang_zh_CN.txt')

def normalize(x):
    x = x.strip()
    while x.endswith('...'): x = x[:-3]
    if x.endswith('\u2026'): x = x[:-1]
    x = re.sub(r'\s+', ' ', x)
    return x.lower()

keys = set()
with io.open(DICT, encoding='utf-8') as f:
    for line in f:
        line = line.rstrip('\n')
        if not line or line.startswith('#') or '\t' not in line:
            continue
        k, v = line.split('\t', 1)
        keys.add(k.strip())
        keys.add(normalize(k))

# UI 字符串出现模式
PATTERNS = [
    (r'new\s+Label\s*\(\s*"((?:[^"\\]|\\.)*)"', 'Label'),
    (r'new\s+Button\s*\(\s*"((?:[^"\\]|\\.)*)"', 'Button'),
    (r'new\s+Checkbox\s*\(\s*"((?:[^"\\]|\\.)*)"', 'Checkbox'),
    (r'new\s+Choice\s*\(\s*\)', None),
    (r'\.setLabel\s*\(\s*"((?:[^"\\]|\\.)*)"', 'setLabel'),
    (r'\.setTitle\s*\(\s*"((?:[^"\\]|\\.)*)"', 'setTitle'),
    (r'\.addCheckbox\s*\(\s*"((?:[^"\\]|\\.)*)"', 'addCheckbox'),
    (r'\.addCheckboxGroup\s*\(\s*\d+\s*,\s*\d+\s*,\s*"((?:[^"\\]|\\.)*)"', 'addCheckboxGroup'),
    (r'\.addStringField\s*\(\s*"((?:[^"\\]|\\.)*)"', 'addStringField'),
    (r'\.addNumericField\s*\(\s*"((?:[^"\\]|\\.)*)"', 'addNumericField'),
    (r'\.addChoice\s*\(\s*"((?:[^"\\]|\\.)*)"', 'addChoice'),
    (r'\.addSlider\s*\(\s*"((?:[^"\\]|\\.)*)"', 'addSlider'),
    (r'\.addMessage\s*\(\s*"((?:[^"\\]|\\.)*)"', 'addMessage'),
    (r'\.addButton\s*\(\s*"((?:[^"\\]|\\.)*)"', 'addButton'),
    (r'new\s+JLabel\s*\(\s*"((?:[^"\\]|\\.)*)"', 'JLabel'),
    (r'new\s+JButton\s*\(\s*"((?:[^"\\]|\\.)*)"', 'JButton'),
    (r'new\s+JCheckBox\s*\(\s*"((?:[^"\\]|\\.)*)"', 'JCheckBox'),
    (r'new\s+JRadioButton\s*\(\s*"((?:[^"\\]|\\.)*)"', 'JRadioButton'),
    (r'new\s+JFrame\s*\(\s*"((?:[^"\\]|\\.)*)"', 'JFrame'),
    (r'new\s+MenuItem\s*\(\s*"((?:[^"\\]|\\.)*)"', 'MenuItem'),
    (r'new\s+CheckboxMenuItem\s*\(\s*"((?:[^"\\]|\\.)*)"', 'CheckboxMenuItem'),
    (r'\.addTab\s*\(\s*"((?:[^"\\]|\\.)*)"', 'addTab'),
    (r'\.setText\s*\(\s*"((?:[^"\\]|\\.)*)"', 'setText'),
]

cjk = re.compile(r'[\u4e00-\u9fff]')
ident = re.compile(r'^[A-Za-z_][A-Za-z0-9_]*$')
results = {}
total = 0

for dirpath, dirnames, filenames in os.walk(os.path.join(ROOT, 'ij')):
    for fn in filenames:
        if not fn.endswith('.java'):
            continue
        path = os.path.join(dirpath, fn)
        rel = os.path.relpath(path, ROOT).replace('\\', '/')
        try:
            src = io.open(path, encoding='utf-8').read()
        except Exception:
            continue
        lines = src.split('\n')
        for pat, kind in PATTERNS:
            if kind is None:
                continue
            for m in re.finditer(pat, src):
                val = m.group(1)
                # 还原源码里的转义，按运行时真实字符串比对
                val = val.replace('\\"', '"').replace("\\'", "'").replace('\\u2026', '\u2026')
                if not val or len(val.strip()) < 2:
                    continue
                if cjk.search(val):        # 已是中文
                    continue
                if ident.match(val):       # 疑似资源名/标识符
                    continue
                def covered(x):
                    if x.strip() in keys or normalize(x) in keys:
                        return True
                    # GenericDialog 会把标签里的 _ 显示为空格
                    if '_' in x and covered(x.replace('_', ' ')):
                        return True
                    if '\n' in x:
                        segs = [y for y in x.split('\n') if y.strip()]
                        return bool(segs) and all(normalize(y) in keys for y in segs)
                    return False
                if covered(val):    # 词典已覆盖（会被 Lang.tr 命中）
                    continue
                if kind == 'setText' and (' ' not in val and len(val) < 4):
                    continue
                lineno = src.count('\n', 0, m.start()) + 1
                results.setdefault(rel, []).append((lineno, kind, val))
                total += 1

out = os.path.join(ROOT, 'i18n_scan.txt')
with io.open(out, 'w', encoding='utf-8') as f:
    f.write('未在词典中的 UI 字符串：%d 条，涉及 %d 个文件\n\n' % (total, len(results)))
    for rel in sorted(results, key=lambda r: -len(results[r])):
        items = results[rel]
        f.write('== %s  (%d)\n' % (rel, len(items)))
        seen = set()
        for lineno, kind, val in items:
            k = (kind, val)
            if k in seen:
                continue
            seen.add(k)
            f.write('  %5d  %-18s %s\n' % (lineno, kind, val))
        f.write('\n')
print('total=%d files=%d -> %s' % (total, len(results), out))
