**Version 1.7.1: 18/06/2019**
pom.xml fixes
Moved CHANGLOG and LICENSE to md rather than txt
Updated license headers to Copyright 2019
Dependencies: apache commons-lang3 3.9
              apache commons-logging 1.2
              apache commons-net 3.6
              jaxb-apit 2.4.0-b180830.0359

**Version 1.7.0: 16/06/201**
Changed to Apache 2.0 license
Added XmlUtilities
Added ListReverser
Added Indirection helpers
Dependencies: apache commons-lang3 3.9
              apache commons-logging 1.2
              apache commons-net 3.6
              jaxb-apit 2.4.0-b180830.0359

**Version 1.6.2: 01/04/2016**
Updated dependencies and update to java 1.8.
Dependencies: apache commons-lang3 3.4
              apache commons-logging 1.2
              apache commons-net 3.4
              igniterealtime smack 4.1.6

**Version 1.61: 20/05/2012 (407)**
Re-implemented com.ceridwen.util.collection.PersistentQueue for backwards compatibility
Dependencies: apache commons lang 3.1
              apache commons net 3.1
              apache commons logging 1.1.1 

**Version 1.60: 25/03/2012 (400)**
Implemented more resilient persistent queue based on com.gaborcselle code
Modified collections Queue and Spooler interfaces to templates
Dependencies: apache commons lang 3.1
              apache commons net 3.1
              apache commons logging 1.1.1 

**Version 1.51: 15/03/2012 (390)**
Use Specification-* where Implementation-* not available
Bugfix: Null pointer exception thrown by LibraryIdentifier.equals for certain libraries
Dependencies: apache commons lang 3.1
              apache commons net 3.1
              apache commons logging 1.1.1 

**Version 1.50: 14/03/2012 (384)**
Version reporting now based on jar manifest files
Queue, Spool etc refactored into Collections subpackage
Update commons net 2.0 to 3.1
Update commons lang3 from 3.0 to 3.1
Dependencies: apache commons lang 3.1
              apache commons net 3.1
              apache commons logging 1.1.1 

**Version 1.40: 22/07/2011 (360)**
Refactored to use StringUtils.isEmpty

**Version 1.30: 13/11/2010 (336)**
Added REST LogHandler
Syslog extracted to dedicated class
Added throttle to AbstractLogHandler
Bugfix: Syslog messages would be truncated
Bugfix: removed hardcoded from address in SMTPLogHandler

**Version 1.20: 3/11/2010 (255)**
Allowed setting of initial delay for PersistentQueue
Updated copyright notices to GPL3

**Version 1.10: 27/10/2010 (123)**
Updated commons logging from 1.1 to 1.1.1
Updated commons net from 1.4.1 to 2.0
Templated classes based on JDK 1.6 warnings

**Version 1.02: 23/1/2007 (93)**
Added peek to queue implementations

**Version 1.01: 3/6/2005 (22)**
Added SyslogLogHandler
Refactored to create an AbstractLogHandler with the main publish method
Exceptions when reading no longer prevent removal
Added JabberLogHandler

**Version 1.00: 25/10/2004 (12**)