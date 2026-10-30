#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""全项目 UI 字符串汉化覆盖扫描。
扫描源码里所有可能直接显示给用户的英文字符串，检查词典是否已覆盖。
输出 report_ui.txt：按文件分组列出未覆盖项。
"""
import io, os, re, collections

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, 'ij')
DICT = os.path.join(SRC, 'i18n', 'IJ_Lang_zh_CN.txt')
OUT = os.path.join(ROOT, 'report_ui.txt')

# (正则, 分组号, 说明)
PATTERNS = [
    (r'\baddMessage\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'addMessage'),
    (r'\baddStringField\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'addStringField'),
    (r'\baddNumericField\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'addNumericField'),
    (r'\baddCheckbox\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'addCheckbox'),
    (r'\baddButton\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'addButton'),
    (r'\baddSlider\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'addSlider'),
    (r'\baddChoice\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'addChoice'),
    (r'\bsetTitle\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'setTitle'),
    (r'\bnew\s+Button\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'new Button'),
    (r'\bnew\s+Label\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'new Label'),
    (r'\bnew\s+Checkbox\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'new Checkbox'),
    (r'\bnew\s+JButton\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'JButton'),
    (r'\bnew\s+JLabel\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'JLabel'),
    (r'\bnew\s+JCheckBox\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'JCheckBox'),
    (r'\bnew\s+JRadioButton\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'JRadioButton'),
    (r'\bnew\s+JMenuItem\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'JMenuItem'),
    (r'\bnew\s+JMenu\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'JMenu'),
    (r'\bnew\s+JCheckBoxMenuItem\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'JCheckBoxMenuItem'),
    (r'\baddTab\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'addTab'),
    (r'\bsetToolTipText\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'tooltip'),
    (r'\bnew\s+GenericDialog\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, 'GenericDialog标题'),
    (r'\bnew\s+NonBlockingGenericDialog\s*\(\s*"((?:[^"\\]|\\.)*)"', 1, '对话框标题'),
]

SKIP_FILES = ('ij/i18n/',)


def normalize(x):
    x = x.strip()
    while x.endswith('...'):
        x = x[:-3]
    if x.endswith('…'):
        x = x[:-1]
    x = re.sub(r'\s+', ' ', x)
    return x.lower()


def unescape(v):
    return (v.replace('\\"', '"').replace("\\'", "'")
             .replace('\\n', '\n').replace('\\u2026', '…').replace('\\\\', '\\'))


def load_dict():
    keys = set()
    with io.open(DICT, encoding='utf-8') as f:
        for line in f:
            if '\t' not in line:
                continue
            k = line.split('\t', 1)[0].strip()
            keys.add(k)
            keys.add(normalize(k))
    return keys


def covered(val, keys):
    if val.strip() in keys or normalize(val) in keys:
        return True
    if '_' in val and covered(val.replace('_', ' '), keys):
        return True
    if '\n' in val:
        segs = [y for y in val.split('\n') if y.strip()]
        return bool(segs) and all(covered(y, keys) for y in segs)
    return False


def main():
    keys = load_dict()
    hits = collections.OrderedDict()
    for dirpath, dirnames, filenames in os.walk(SRC):
        if any(s in dirpath.replace('\\', '/') for s in SKIP_FILES):
            continue
        for fn in sorted(filenames):
            if not fn.endswith('.java'):
                continue
            path = os.path.join(dirpath, fn)
            rel = os.path.relpath(path, ROOT).replace('\\', '/')
            src = io.open(path, encoding='utf-8', errors='replace')
            for i, line in enumerate(src, 1):
                if line.lstrip().startswith('//') or line.lstrip().startswith('*'):
                    continue
                for pat, grp, kind in PATTERNS:
                    for m in re.finditer(pat, line):
                        val = unescape(m.group(grp))
                        if len(val.strip()) < 2:
                            continue
                        if not re.search(r'[A-Za-z]{3}', val):
                            continue
                        if covered(val, keys):
                            continue
                        hits.setdefault(rel, []).append((i, kind, val))
    lines = []
    total = 0
    for rel in sorted(hits):
        lines.append('== %s  (%d)' % (rel, len(hits[rel])))
        for i, kind, val in hits[rel]:
            lines.append('    %-6d %-22s %s' % (i, kind, val.replace('\n', '\\n')))
            total += 1
        lines.append('')
    head = '未汉化的 UI 字符串：%d 条，涉及 %d 个文件\n' % (total, len(hits))
    io.open(OUT, 'w', encoding='utf-8', newline='\n').write(head + '\n'.join(lines))
    print(head)


if __name__ == '__main__':
    main()
