#!/usr/bin/env bash
# wait-for-mysql.sh

host="$1"
port="$2"
shift 2
cmd="$@"

echo "Waiting for MySQL at $host:$port…"
while ! nc -z "$host" "$port"; do
  sleep 1
done
echo "MySQL is up — executing command: $cmd"
exec $cmd
