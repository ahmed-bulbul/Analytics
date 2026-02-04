const { existsSync } = require('fs');
const { spawnSync } = require('child_process');

function hasNodeModules() {
  return existsSync('node_modules');
}

function run(cmd, args) {
  const res = spawnSync(cmd, args, { stdio: 'inherit', shell: process.platform === 'win32' });
  process.exit(res.status ?? 1);
}

if (!hasNodeModules()) {
  console.log('Skipping build: node_modules not found. Run `npm install` first.');
  process.exit(0);
}

run('npm', ['run', 'build']);
