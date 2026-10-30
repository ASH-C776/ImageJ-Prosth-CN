#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""把 tools/lang_extra*.txt 中的译文合并进主词典。
每行格式：key|value，多行文本可写 key1|val1|key2|val2。
已存在的 key 自动跳过。
"""
import io, os, re, sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DICT = os.path.join(ROOT, 'ij', 'i18n', 'IJ_Lang_zh_CN.txt')


def norm(x):
    x = x.strip()
    while x.endswith('...'):
        x = x[:-3]
    if x.endswith('\u2026'):
        x = x[:-1]
    x = re.sub(r'\s+', ' ', x)
    return x.lower()


def main():
    files = sys.argv[1:]
    if not files:
        files = [os.path.join(ROOT, 'tools', 'lang_extra%d.txt' % i) for i in (1, 2, 3)]
        files = [f for f in files if os.path.exists(f)]
    lines = io.open(DICT, encoding='utf-8').read().split('\n')
    exist = set()
    existn = set()
    for l in lines:
        if '\t' in l:
            k = l.split('\t', 1)[0]
            exist.add(k.strip())
            existn.add(norm(k))
    add = []
    skipped = []
    for f in files:
        if not os.path.exists(f):
            continue
        for raw in io.open(f, encoding='utf-8').read().split('\n'):
            if '|' not in raw:
                continue
            parts = raw.split('|')
            for i in range(0, len(parts) - 1, 2):
                k = parts[i].strip()
                v = parts[i + 1].strip()
                if not k or not v:
                    continue
                if '\t' in k or '\t' in v:
                    print('TAB in', k)
                    continue
                if k in exist or norm(k) in existn:
                    skipped.append(k)
                    continue
                exist.add(k)
                existn.add(norm(k))
                add.append(k + '\t' + v)
    while lines and lines[-1].strip() == '':
        lines.pop()
    out = '\n'.join(lines) + '\n' + '\n'.join(add) + '\n'
    io.open(DICT, 'w', encoding='utf-8', newline='\n').write(out)
    print('added=%d skipped=%d total=%d' % (len(add), len(skipped), len(out.split('\n'))))


if __name__ == '__main__':
    main()
