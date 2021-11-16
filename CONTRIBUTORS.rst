.. 2>/dev/null
 names () 
 { 
 echo -e "\n exit;\n**Contributors (sorted by number of commits):**\n";
 git log --format='%aN:%aE' origin/master | grep -Ev "(anonymous:|FYG_.*_bot_ignore_me)" | sed 's/@users.github.com/@users.noreply.github.com/g' | awk 'BEGIN{FS=":"}{match ($1, /^(%)?(.*)/, n) ; ct[n[2]]+=1; if (n[1] ~ /%/ || e[n[2]] == "" ) { e[n[2]]=$2}}END{for (i in e) { n[i]=e[i];c[i]+=ct[i] }; for (a in e) print c[a]"\t* "a" <"n[a]">";}' | sort -n -r | cut -f 2-
 }
 quine () 
 { 
 { 
 echo ".. 2>/dev/null";
 declare -f names | sed -e 's/^[[:space:]]*/ /';
 declare -f quine | sed -e 's/^[[:space:]]*/ /';
 echo -e " quine\n";
 names;
 echo -e "\n*To update the contributors list just run this file with bash. Prefix a name with % in .mailmap to set a contact as preferred*"
 } > CONTRIBUTORS.rst;
 exit
 }
 quine


 exit;
**Contributors (sorted by number of commits):**

* Peter Moser <p.moser@noi.bz.it>
* Patrick Bertolla <p.bertolla@noi.bz.it>
* Jenkins automated commits <no-reply@opendatahub.bz.it>
* Alex Lanz <alex@krumer.it>
* dependabot[bot] <49699333+dependabot[bot]@users.noreply.github.com>
* Martin Malfertheiner <martin.malfertheiner@gmail.com>
* Stefano David <stefano.david.bz@gmail.com>
* bertolla <patrick.bertolla@gmail.com>
* Matthias Dieter Wallnöfer <matthias.wallnoefer@lugbz.org>
* Patrick Ohnewein <p.ohnewein@noi.bz.it>
* PaddyBee <patrick.bertolla@gmail.com>
* Davide Montesin <d@vide.bz>

*To update the contributors list just run this file with bash. Prefix a name with % in .mailmap to set a contact as preferred*
