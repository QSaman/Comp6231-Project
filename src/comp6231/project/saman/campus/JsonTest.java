package comp6231.project.saman.campus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class Student
{
	public String name;
	public int age;
}

public class JsonTest {

	public JsonTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		Student student = new Student();
		student.name = "foo";
		student.age = 22;
		Gson gson = new Gson();
		String json_str = gson.toJson(student);
		System.out.println(json_str);
		
		JsonParser json_parser = new JsonParser();
		JsonElement json_element = json_parser.parse(json_str);
		
		JsonObject json_object = json_element.getAsJsonObject();
		json_object.keySet();
		System.out.println(json_object.get("name"));

	}

}
