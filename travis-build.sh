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

if [[ $TRAVIS_TAG =~ ^v[[:digit:]] && $TRAVIS_BRANCH =~ ^v[[:digit:]] && $TRAVIS_PULL_REQUEST == 'false' ]]; then
  if [[ -z $SKIP_BINTRAY || $SKIP_BINTRAY == 'false' ]]; then
	  ./gradlew bintrayUpload --stacktrace
  else
    echo "Skipping Bintray deployment: SKIP_BINTRAY is true"
  fi

	./gradlew docs --stacktrace

	git config --global user.name "$GIT_NAME"
	git config --global user.email "$GIT_EMAIL"
	git config --global credential.helper "store --file=~/.git-credentials"
	echo "https://$GH_TOKEN:@github.com" > ~/.git-credentials

  version=${TRAVIS_TAG:1}

  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/driverpt/grails-middleware.git gh-pages > /dev/null

  cd gh-pages
  mkdir -p $version
  rm -rf latest
  ln -s $version latest
  mv ../build/docs/manual/* $version/
  git add $version

  git commit -a -m "Updating docs for Travis build: https://travis-ci.org/$TRAVIS_REPO_SLUG/builds/$TRAVIS_BUILD_ID"

	git push -q origin > /dev/null
fi
