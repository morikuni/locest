import sys.process.Process
import java.io.File

val sqlDir = new File("./sql/")
val host = "192.168.33.10"
val user = "locest"
val database = "locest"

val password = System.console.readPassword("%s", "Password:").mkString("")
println(password)

sqlDir.listFiles.foreach { file =>
  println(file)
  Process(s"psql -h ${host} -d ${database} -U ${user} -f ${file.getAbsolutePath}", None, "PGPASSWORD" -> password) !!
}