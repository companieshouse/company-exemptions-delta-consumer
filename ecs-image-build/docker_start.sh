#!/bin/bash

PORT=8080

exec java -jar -Dserver.port="${PORT}" "company-exemptions-delta-consumer.jar"