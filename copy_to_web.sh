export COPYFILE_DISABLE=true
find . -name .DS_Store -exec rm "{}" ";"
tar --exclude vtv2006 --exclude documents --exclude hdcookbook_repo --exclude hdcookbook --exclude moomtastic --exclude javasoft --exclude tass --exclude palestine --exclude hat --exclude copy_to_web.sh --exclude htaccess_copy --exclude pictures --exclude movies --exclude slides -c -f - * | ssh jovial.com -l jovialco "(mkdir new ; cd new ; tar xvf - ; cd .. ; bin/publish new public_html/jovial)"
