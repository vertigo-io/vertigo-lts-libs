Version history
===============

Running 4.3.0
----------------------

more to come :)


Release 4.2.0 - 2024/08/22
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-400-to-410)
* [DataModel] Impact v4.2.0 renaming DtObject => DataObject & co

Release 3.6.1 - 2024/06/03
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-360-to-361)
A step version focus on Struts2 MCS.

* CSP vertigo-struts2 : transmission du nonce via <@s.script> by @vpkg in #6
* Update a-close.ftl by @vpkg in #7
* Update Struts2 6.3.0 by @vpkg in #8
* text.ftl avec no_esc by @vpkg in #9

Release 4.1.0 - 2023/11/09
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-400-to-410)
* Minor changes

Release 4.0.0 - 2023/08/17
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-360-to-400)
* **Deprecated and Removed Struts2**
* **Update to JDK 17**
* Add `vertigo-datafactory-plugin-elasticsearch_7_17` to prepare migration to 8.x (WIP)
- [All] update libs 
  - h2 2.1.214 -> 2.2.220

Release 3.6.0 - 2023/05/04
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-350-to-360)
- [All] update libs 
  - struts2 6.0.3 -> 6.1.2
  - struts2-jquery-plugin 5.0.0 -> 5.0.2
  - jetty 9.4.49 -> 9.4.50
  - selenium 4.4.0 -> 4.7.2  
  
Release 3.5.0 - 2023/01/06
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-340-to-350)
_No changes this time_

Release 3.4.0 - 2022/10/12
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-330-to-340)
- [Struts2] List YesNo isn't a Entity => can't use MDL use static List instead
- [Search] fix UID, id type is infered by caller
- [Struts2] Fix big MdList (can't build uiObjetIndex)
- [Struts2] add escape < >
- [Struts2] Update tags to 6.0.3
  - checkboxlist (evaluate_dynamic_attributes true)
  - form (onsubmit output js)
  - radiomap (change escape, evaluate_dynamic_attributes true)
  - fielderror (message no esc)
- [All] Updated libs
  - h2 2.1.210 -> 2.1.214
  - struts2 2.5.28.3 -> 6.0.3
  - jetty 9.4.44 -> 9.4.49
  - selenium 4.1.1 -> 4.4.0
  - htmlunit-driver 3.56.0 -> 3.64.0

Release 3.3.0 - 2022/02/03
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-320-to-330)
* [Account] ReAdd previous XML security system (was removed in 2.0.0), with permissions definition in XML : **vertigo-account-plugin-authorization-basic**
!>__WARN_ AuthorizationManager and AuthorizationBasicManager aren't linked (permissions in XML aren't only active with AuthorizationBasicManager)
* [Account] Change xml auth parser to support the | in permission's operation (Operation is used as a regex, the | mean OR)
* [Struts2] Fix UiListUnmodifiable
* [Struts2] Fix tags templates (dev retex : autocomplete, tooltip, radiomap, select, controllabel, controlfooter, div)
* [Struts2] Add contextRef clean (dev retex) _Use with care, may refresh page for most cases_
* [Ui] UiList getById compute index by using keyField parameter
* [Struts2] initUiObjectByKeyIndex overidedable
* [Struts2] Rollback UiList lazy index in initUiObjectByKeyIndex
* [struts2] use keyField on DtDefinition when available for UiListUnmodifiable
* [datafactory-lts] fix RamLuceneIndexPlugin with ValueObjects
* [Struts2] Update ftl components (from struts2 version)
* [All] Updated libs
  - h2 1.4.200 -> 2.1.210
  - jetty 9.4.40 -> 9.4.44
  - struts2 2.5.26 -> 2.5.28.3
  - lucene 8.7.0 -> 8.11.1
  - commons-io 2.7 -> 2.11.0
  - slf4j-log4j12 1.7.30 -> 1.7.33
  - selenium 3.141.59 -> 4.1.1
  - htmlunit-driver 2.50.0 -> 3.56.0


Release 3.2.0 - 2021/06/21
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-311-to-320)
* [Struts2] Fixed contextKey in StrutsUiListModifiable  
* [All] Updated libs
  - jetty 9.4.35 -> 9.4.40
  - selenium htmlunit-driver 2.46.0 -> 2.50.0

Release 3.1.1 - 2021/02/22
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-310-to-311)
_No changes this time_

Release 3.1.0 - 2021/02/05
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-300-to-310)
* [All] Code cleaning, refactoring and documenting 
* [All] Updated libs
  - log4j 2.13.3 -> 2.14.0
  - lucene 8.6.2 -> 8.7.0
  - jetty 9.4.31.v20200723 -> 9.4.35.v20201120
  - selenium htmlunit-driver 2.45.0 -> 2.46.0
  - struts2 2.5.25 -> 2.5.26


Release 3.0.0 - 2020/11/20
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-210-to-300)
 * First release : this repository keep 'old' Vertigo modules for Long Term Support
 * Add DataFactory plugin for ElasticSearch 5.6.x
 * Add Struts2 UI module
