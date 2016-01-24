#!/usr/bin/env bash

set -e

rm -rf build

./gradlew -q clean check install --stacktrace

integration-test-app/run-integration-tests.sh

if [[ $TRAVIS_DEBUG == 'true' ]]; then
    echo "TRAVIS_TAG: $TRAVIS_TAG"
    echo "TRAVIS_BRANCH: $TRAVIS_BRANCH"
    echo "TRAVIS_PULL_REQUEST: $TRAVIS_PULL_REQUEST"
fi

if [[ -n $TRAVIS_TAG && $TRAVIS_BRANCH == 'master' && $TRAVIS_PULL_REQUEST == 'false' ]]; then
	./gradlew bintrayUpload --stacktrace

	./gradlew docs --stacktrace

	git config --global user.name "$GIT_NAME"
	git config --global user.email "$GIT_EMAIL"
	git config --global credential.helper "store --file=~/.git-credentials"
	echo "https://$GH_TOKEN:@github.com" > ~/.git-credentials

	git checkout gh-pages

	mv build/docs/manual/* .

	git commit -a -m "Updating docs for Travis build: https://travis-ci.org/$TRAVIS_REPO_SLUG/builds/$TRAVIS_BUILD_ID"

	git push origin
fi
