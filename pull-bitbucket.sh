git remote add bitbucket ../diplomarbeit
git pull -s recursive -Xsubtree=project/mascherl --squash bitbucket master

git remote remove bitbucket
git push origin master

