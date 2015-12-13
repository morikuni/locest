import java.io.{File, PrintWriter}
import scala.xml.XML

val gmlDir = new File("./area_data/")
val outDir = new File("./sql/")
if(!outDir.exists){
  outDir.mkdir
}

gmlDir.listFiles.filter(_.getName.contains(".gml")).foreach { file =>
  val out = new PrintWriter(new File(outDir, file.getName.replace(".gml", ".sql")))
  val gml = XML.loadFile(file)

  val fm = gml \\ "featureMember"

  val firstElement = fm.head
  val prefectureId = (firstElement \\ "KEN").text
  val cityId = (firstElement \\ "CITY").text
  val prefectureName = (firstElement \\ "KEN_NAME").text
  val gstName = (firstElement \\ "GST_NAME").text
  val cssName = (firstElement \\ "CSS_NAME").text

  val area_id = prefectureId+cityId
  val name = Array(prefectureName, gstName, cssName).filter(_.nonEmpty).mkString(" ")

  out.println("BEGIN;")

  out.println(s"INSERT INTO area (area_id, name, center) VALUES (${area_id}, '${name}', ST_GeomFromText('POINT(-1 -1)', 4326));")

  (fm \\ "posList").foreach{ posList =>
    val polygon = posList.text.split(" ").reverse.grouped(2).map(_.mkString(" ")).mkString(", ")
    out.println(s"INSERT INTO shape (area_id, shape) VALUES (${area_id}, ST_GeomFromText('POLYGON((${polygon}))', 4326));")
  }

  out.println(
    s"""UPDATE area
       |SET center = ST_Centroid(ST_Collect(polygons))
       |FROM
       |  (SELECT array_agg(shape) as polygons FROM shape WHERE area_id = ${area_id}) as x
       |WHERE area_id = ${area_id};""".stripMargin
  )

  out.println("COMMIT;")
  out.close
}