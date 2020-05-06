#!/bin/bash

# Only deploy master and develop branch to sonatype
if [[ $TRAVIS_REPO_SLUG == "deveth0/httpclient-spring-boot-starter" ]] && [[ "$TRAVIS_PULL_REQUEST" == "false" ]] && [[ "$TRAVIS_BRANCH" == "feature/travis_sonatype" || "$TRAVIS_BRANCH" == "develop" ]]; then
  if [[ $TRAVIS_JDK_VERSION == "openjdk11" ]]; then
    gpg2 --allow-secret-key-import --keyring=$TRAVIS_BUILD_DIR/pubring.gpg --no-default-keyring --import ./travis/sonatype_signing.asc
    echo -e "Successfully imported GPG keys"

    echo - "Seckeys"
    gpg2 --keyring=$TRAVIS_BUILD_DIR/pubring.gpg --list-secret-keys
    echo -e "Pubkeys"
    gpg2 --keyring=$TRAVIS_BUILD_DIR/pubring.gpg --list-public-keys

    mvn deploy -DskipTests=true -Prelease-profile --settings ./travis/maven-settings.xml -Dgpg.useAgent=false -Dgpg.executable=gpg2 -Dgpg.keyname=05E8EEC0 -Dgpg.passphrase=$PASSPHRASE -Dgpg.publicKeyring=$TRAVIS_BUILD_DIR/pubring.gpg -Dgpg.secretKeyring=$TRAVIS_BUILD_DIR/pubring.gpg
    echo -e "Successfully deployed to sonatype"

  fi
else
  echo "Travis deployment skipped"
fi
