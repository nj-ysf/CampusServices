#!/usr/bin/env bash
# Lance CampusServices via Maven (Linux / macOS)
# Prérequis : Java 11+ et Maven installés
set -e
cd "$(dirname "$0")"
mvn javafx:run
