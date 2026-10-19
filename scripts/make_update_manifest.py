"""Create a GitHub-release update manifest from an actual compiled APK."""
import argparse
import hashlib
import json
from pathlib import Path
import re

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('apk', type=Path)
parser.add_argument('--url', required=True, help='Public HTTPS download URL for this APK')
parser.add_argument('--output', type=Path, default=Path('release/update.json'))
parser.add_argument('--notes', default='WCW Chronology update.')
args = parser.parse_args()
if not args.url.startswith('https://'):
    parser.error('--url must be HTTPS')
build = (Path(__file__).resolve().parents[1] / 'app/build.gradle').read_text(encoding='utf-8')
manifest = {'versionCode': int(re.search(r'versionCode\s+(\d+)', build)[1]),
            'versionName': re.search(r"versionName\s+'([^']+)'", build)[1],
            'apkUrl': args.url, 'sha256': hashlib.sha256(args.apk.read_bytes()).hexdigest(),
            'notes': args.notes}
args.output.parent.mkdir(parents=True, exist_ok=True)
args.output.write_text(json.dumps(manifest, indent=2) + '\n', encoding='utf-8')
print(str(args.output))
