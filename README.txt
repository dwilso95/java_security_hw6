Requires: JDK 9, Maven

Build: 'mvn clean package'

Run C14N Demo: 'java -cp target\java-security-hw6-0.0.1-SNAPSHOT.jar edu.jhu.dwilso95.C14NDemo src/main/resources/directory.xml'

Run OrderSignerVerifier: 'java -cp target\java-security-hw6-0.0.1-SNAPSHOT.jar edu.jhu.dwilso95.OrderSignerVerifier sv src/main/resources/order.xml orderSigned.xml'