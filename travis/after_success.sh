#!/bin/bash

# Only deploy master and develop branch to sonatype
if [[ $TRAVIS_REPO_SLUG == "deveth0/httpclient-spring-boot-starter" ]] && [[ "$TRAVIS_PULL_REQUEST" == "false" ]] && [[ "$TRAVIS_BRANCH" == "master" || "$TRAVIS_BRANCH" == "develop" ]]; then
  if [[ $TRAVIS_JDK_VERSION == "openjdk11" ]]; then
    gpg2 --keyring=$TRAVIS_BUILD_DIR/pubring.gpg --no-default-keyring --import ./travis/sonatype_signing.asc
    gpg2 --allow-secret-key-import --keyring=$TRAVIS_BUILD_DIR/secring.gpg --no-default-keyring --import ./travis/sonatype_signing.asc
    echo -e "Successfully imported GPG keys"
    gpg --list-secret-keys --keyring=$TRAVIS_BUILD_DIR/secring.gpg

    mvn deploy -DskipTests=true -Prelease-profile --settings ./travis/maven-settings.xml -Dgpg.executable=gpg2 -Dgpg.keyname=D4E7E9033BBC3785DB28D8DBBE5F392A05E8EEC0 -Dgpg.passphrase=$PASSPHRASE -Dgpg.publicKeyring=$TRAVIS_BUILD_DIR/pubring.gpg -Dgpg.secretKeyring=$TRAVIS_BUILD_DIR/secring.gpg
    echo -e "Successfully deployed to sonatype"

  fi
else
  echo "Travis deployment skipped"
fi
