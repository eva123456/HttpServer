JAVAC = javac
JAR = jar
JARFLAGS = cvfm
MANIFEST = MANIFEST
SOURCES = sources/HttpServer.java sources/MyThread.java sources/MyResponse.java sources/MyRequest.java sources/FileStorage.java

all: highload

highload: 
	$(JAVAC) $(SOURCES)

jar: 
	$(JAVAC) $(SOURCES)
	$(JAR) $(JARFLAGS) highload.jar $(MANIFEST) sources/*.class
