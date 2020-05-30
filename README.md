
This is a convenience plugin to sign jars and WAR files and set manifest attributes.

It can be used to sign jars and WAR files before deploying to production. 

It can also be used to resign webstart WAR files in case a certificate is
about to expire, or to ensure every application jar is signed with the same 
certificate, and at the same time make sure the `Permissions: all-permissions` 
attribute required since JDK 1.8 is in place everywhere.

The implementation simply wraps the `jarsigner` executable provided with the JDK.
Jar files are resigned in-place using zip file systems but WAR files are expanded
and replaced if every jar is processed successfully.

