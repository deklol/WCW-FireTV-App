"""Validate editable source IDs and the bundled catalogue without third-party packages."""
import json
from pathlib import Path
import re
from urllib.parse import urlparse

root = Path(__file__).resolve().parents[1]
config = json.loads((root / 'sources.json').read_text(encoding='utf-8'))
assert config['schemaVersion'] == 1
assert re.fullmatch(r'UC[A-Za-z0-9_-]{22}', config['youtube']['channelId'])
assert 1 <= config['archive']['collectionsPerScan'] <= 50
for provider, key, pattern in [('youtube', 'playlists', r'[A-Za-z0-9_-]{10,80}'),
                               ('archive', 'collections', r'[A-Za-z0-9][A-Za-z0-9_.-]{0,99}')]:
    entries = config[provider][key]
    assert len({item['id'] for item in entries}) == len(entries), 'Duplicate source ID'
    for item in entries:
        assert re.fullmatch(pattern, item['id']), 'Invalid source identifier: ' + item['id']
        assert item.get('name'), 'Give each source a readable name'
        assert isinstance(item.get('enabled', True), bool)
direct = config['youtube'].get('videos', [])
assert len({v['id'] for v in direct}) == len(direct), 'Duplicate direct video'
for entry in direct:
    assert re.fullmatch(r'[A-Za-z0-9_-]{11}', entry['id'])
    assert re.fullmatch(r'UC[A-Za-z0-9_-]{22}', entry['channelId'])
    assert re.fullmatch(r'https://www\.youtube\.com/@[A-Za-z0-9_.-]+', entry['authorUrl'])
    assert '1983-01-01' <= entry['date'] <= '2001-03-26'
    assert entry['duration'] >= (2700 if entry['show'] == 'PPV' else 1200)
data = json.loads((root / 'app/src/main/assets/catalogue.json').read_text(encoding='utf-8'))
rows = data['episodes']
assert len({row['id'] for row in rows}) == len(rows)
assert [row['date'] for row in rows] == sorted(row['date'] for row in rows)
for row in rows:
    assert '1983-01-01' <= row['date'] <= '2001-03-26'
    assert row['sources']
    for source in row['sources']:
        if source['type'] == 'archive':
            for url in source.get('urls', [source.get('url')]):
                parsed = urlparse(url)
                assert parsed.scheme == 'https' and parsed.hostname == 'archive.org'
                assert parsed.path.startswith('/download/')
        else:
            assert source['type'] == 'youtube'
            assert re.fullmatch(r'[A-Za-z0-9_-]{11}', source['videoId'])
            assert re.search(r'\bFULL (?:EPISODE|EVENT)\b', source['title'], re.I)
            assert not re.search(r'FULL MATCH|watch party|compilation', source['title'], re.I)
            assert source['duration'] >= (1200 if 'FULL EPISODE' in source.get('title', '').upper() else 2700)
print(f'Valid: {len(config["youtube"]["playlists"])} playlists, {len(direct)} individual videos, '
      f'{len(config["archive"]["collections"])} Archive collections, {len(rows)} seed episodes.')
