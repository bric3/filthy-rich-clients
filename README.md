# Filthy Rich Clients

This repository hosts all the code examples from the
book called Filthy Rich Clients, written by Chet Haase
and Romain Guy.

You can find more information about the book here:
http://filthyrichclients.org/

![Filthy Rich Clients](http://filthyrichclients.org/images/book.png)

All the examples are licensed under a 3-clause BSD
license.
                                                                    
## Moved project descriptor to Gradlew

The original code required an old Netbeans distribution; also the projects
had jars directly under the project structure (under `lib/`), sources and resources 
under the same `src` folder. And finally, the java version ranged from 1.4 to 1.6.
The project is now configured with Gradle, using standard Java project structure
(`src/main/java`, `src/main/resources`), as well as the latest Java version: 20. 

Note, however, dependency sources were on the defunct http://java.net, and are hard 
to come by in the same version. For now the jars are left in place.

Now one can run an example this way

```bash
./gradlew :AnimatedTransitions:ImageBrowser:run
```

To discover the available projects, use 

```bash
./gradlew projects
```

