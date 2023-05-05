Version history
===============

Running 4.0.0
----------------------
- **[ElasticSearch] No more support of ES 5.x.x**
- **[ElasticSearch] Add support of ES 7.x.x**

more to come :)


Release 3.6.0 - 2023/05/04
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-350-to-360)
- [Struts2] update libs 
  - struts2 6.0.3 -> 6.1.2
  - struts2-jquery-plugin 5.0.0 -> 5.0.2
  - jetty 9.4.49 -> 9.4.50
  - selenium 4.4.0 -> 4.7.2
  
  
Release 3.5.0 - 2023/01/06
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-340-to-350)
__no changes__

Release 3.4.0 - 2022/10/12
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-330-to-340)
- [ElasticSearch] fix UID, id type is infered by caller
- [Struts2] Fix big MdList (can't build uiObjetIndex)
- [Struts2] add escape < > in autocomplete json data
- [Struts2] update libs 
  - struts2-core 2.5.28.3 -> 6.0.3
- [Struts2] Update tags to 6.0.3 
  - checkboxlist (evaluate_dynamic_attributes true)
  - form (onsubmit output js)
  - radiomap (change escape, evaluate_dynamic_attributes true)
  - fielderror (message no esc)

Release 3.3.0 - 2022/02/03
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-320-to-330)
* [Account] Add authorization model used before vertigo 2.0.0. With Role and Permissions declared in XML files (`vertigo-account-plugin-authorization-basic`)
* [DataFactory] Fix SearchServicesPlugin API, but don't support multiple Index
* [DataFactory] fix RamLuceneIndexPlugin with ValueObjects
* [Struts2] Fix UiListUnmodifiable when no `keyFieldName`
* [Struts2] UiList getById compute index by using keyField parameter
* [Struts2] use keyField on DtDefinition when available for UiListUnmodifiable
* [Struts2] initUiObjectByKeyIndex overidedable
* [Struts2] Fix tags templates, from project's dev retex
* [Struts2] Add contextRef clear
* [Struts2] Update pom.xml versions 
  - struts2 2.5.26 -> 2.5.28.3
  - h2 1.4.200 -> 2.1.210
  - lucene 8.7.0 -> 8.11.1
  - commons-io 2.7 -> 2.11.0
  - slf4j-log4j12 1.7.30 -> 1.7.33
  - selenium 3.141.59 -> 4.1.1
  - htmlunit-driver 2.50.0 -> 3.56.0
- [Struts2] Update tags to 2.5.28.3


Release 3.2.0 - 2021/06/21
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-311-to-320)
* [Struts2] Fixed contextKey in StrutsUiListModifiable

Release 3.1.1 - 2021/02/22
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-310-to-311)
__no changes__

Release 3.1.0 - 2021/02/05
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-300-to-310)
* [Struts2] Fix CropHtmlFilter (may miss some end tag)
* [All] Updated libs
  - jetty 9.4.31.v20200723 -> 9.4.35.v20201120
  - struts2 2.5.25 -> 2.5.26


Release 3.0.0 - 2020/11/20
----------------------
[Migration help](https://github.com/vertigo-io/vertigo/wiki/Vertigo-Migration-Guide#from-210-to-300)
* **creation of this LTS Extension repository **
* [Dynamo] Removed elastic search deprecated 'type'
* [Struts2] Updated lib + fix tests with htmlUnit-driver
  - struts2 2.5.20 -> 2.5.22
  - c3p0 0.9.5.4 -> 0.9.5.5
  - org.eclipse.jetty 9.4.21 -> 9.4.31
  - org.apache.lucene 8.2.0 -> 8.5.1
  - commons-io 2.6 -> 2.7
  - slf4j-log4j12 1.7.28 -> 1.7.30

