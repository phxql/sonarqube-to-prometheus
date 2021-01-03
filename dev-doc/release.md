# Release process

```shell
export VERSION="0.2.0"
mvn versions:set -DgenerateBackupPoms=false -DnewVersion="$VERSION"
mvn clean package
git commit -am "Release version $VERSION"
git tag "v$VERSION"
mvn versions:set -DgenerateBackupPoms=false -DnextSnapshot"
```
