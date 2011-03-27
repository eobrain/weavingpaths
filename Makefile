APP=worms
JS=$(APP)/public/javascripts
LINTED=\
 $(JS)/index.linted\
 \

run: jslint
	./play-1.1.1/play $@ $(APP)

test: jslint
	./play-1.1.1/play $@ $(APP)

update: jslint $(APP)/lib 

clean:
	./play-1.1.1/play $@ $(APP)

jslint: $(LINTED)

$(APP)/lib: jar-fetcher/project/build/WormsJarFetcher.scala jar-fetcher/lib_managed/scala_2.8.0/compile
	cd jar-fetcher; ./sbt update
	rsync -r jar-fetcher/lib_managed/scala_2.8.0/compile/ $@/

jar-fetcher/lib_managed/scala_2.8.0/compile:
	cd jar-fetcher; ./sbt update

.SUFFIXES: .js .linted

JSLINT=java -classpath build/js.jar org.mozilla.javascript.tools.shell.Main build/jslint.js


%.linted : %.js
	$(JSLINT) $<
	touch $@



