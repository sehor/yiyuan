package yiyuan.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutoJOPO {
    private String beanName; // 第一个字母大写
    private String packagePath;
    private String filePath;
    private String originName;//第一个字母小写
    private List<Field> fields = new ArrayList<>();

    private final String newLine = "\r\n";
    private final String blank = " ";
    private final String nio = "\"\"";

    /*
      是否是entity
       default:true
     */
    private boolean isEntity = true;

    /*
    是否添加swagger注解
    default:true
  */
    private boolean addSwagger = true;

    /*
    是否添加validation注解
    default:true
  */
    private boolean addValidation = true;

    public AutoJOPO addField(String access, String type, String name) {
        this.fields.add(new Field(access, type, name));
        return this;
    }

    public String buildRepositoryFile() throws IOException {
        StringBuilder repositoryFileString = new StringBuilder();
        repositoryFileString.append("package " + this.packagePath + ";" + newLine);
        repositoryFileString.append("import org.springframework.data.jpa.repository.JpaRepository;" + newLine +
                "import org.springframework.stereotype.Repository;" + newLine
        );

        repositoryFileString.append("@Repository" + newLine);
        repositoryFileString.append("public interface " + this.beanName + "Repository extends JpaRepository<" + this.beanName + "," + getIdType() + ">," +
                this.beanName + "DataHelper {" + newLine);

        repositoryFileString.append("}");

        writeToFile(this.beanName + "Repository.java", repositoryFileString.toString());
        return repositoryFileString.toString();
    }

    public String buildRepositoryHelpFile() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package " + this.packagePath + ";" + newLine);

        stringBuilder.append("public interface " + this.beanName + "DataHelper {" + newLine);

        stringBuilder.append("}");

        writeToFile(this.beanName + "DataHelper.java", stringBuilder.toString());
        return stringBuilder.toString();
    }

    public String buildRepositoryImplFile() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package " + this.packagePath + ";" + newLine);

        stringBuilder.append("public class " + this.beanName + "RepositoryImpl implements " + this.beanName + "DataHelper" + " {" + newLine);

        stringBuilder.append("}");

        writeToFile(this.beanName + "RepositoryImpl.java", stringBuilder.toString());
        return stringBuilder.toString();
    }

    public String buildControllerFile() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package " + this.packagePath + ";" + newLine);

        stringBuilder.append("import io.swagger.annotations.Api;" + newLine);
        stringBuilder.append("import io.swagger.annotations.ApiImplicitParam;" + newLine);
        stringBuilder.append("import io.swagger.annotations.ApiOperation;" + newLine);
        stringBuilder.append("import io.swagger.annotations.ApiResponse;" + newLine);
        stringBuilder.append("import org.springframework.beans.factory.annotation.Autowired;" + newLine);
        stringBuilder.append("import org.springframework.ui.Model;" + newLine);
        stringBuilder.append("import org.springframework.web.bind.annotation.*;" + newLine);

        stringBuilder.append("@Api(tags = \"" + this.beanName + " 接口测试\")" + newLine);//add swagger test document
        stringBuilder.append("@RestController" + newLine);  //add default annotation
        stringBuilder.append("@RequestMapping(\"/" + this.beanName + "\")" + newLine); //add default mapping pattern

        stringBuilder.append("public class " + this.beanName + "Controller" + " {" + newLine);
        stringBuilder.append("@Autowired" + newLine);   //add default service interface
        stringBuilder.append(this.beanName + "Service" + " service;" + newLine + newLine); //add default service interface

        // add example method
        stringBuilder.append("@ApiOperation(value = \"example\",notes = \"this is a example\")" + newLine);
        stringBuilder.append("@ApiResponse(message = \"访问成功\",code = 200)" + newLine);
        stringBuilder.append("@ApiImplicitParam(value= \"str\",type = \"path\",required = false,name = \"path variable\")" + newLine);
        stringBuilder.append("@GetMapping(\"/example/{str}\")" + newLine);
        stringBuilder.append("public String example(@PathVariable(value = \"str\") String str) { " + newLine +

                " return \"你好，\"" + "+ str;" + newLine +
                " }" + newLine);


        // add some default apis:  

        //add one   
        stringBuilder.append("    @PostMapping(\"/add\")\n" +
                "    public "+this.beanName+" add(@RequestBody "+this.beanName+" "+this.originName+"){\n" +
                "\n" +
                "         return service.add"+this.beanName+"("+this.originName+");\n" +
                "    }" + newLine);

        //get one by id
        stringBuilder.append("    @GetMapping(\"/get/{id}\")\n" +
                "    public "+this.beanName+" get"+this.beanName+"(@PathVariable(value = \"id\") Integer id){\n" +
                "      return service.get"+this.beanName+"(id);\n" +
                "    }" + newLine);

        //update
        stringBuilder.append("   @PutMapping(\"/update\")\n" +
                "    public "+this.beanName+" update(@RequestBody "+this.beanName+" "+this.originName+"){\n" +
                "     return service.update"+this.beanName+"("+this.originName+");\n" +
                "    }" + newLine);

        
        //delete one by id
        stringBuilder.append("    @DeleteMapping(\"/delete/{id}\")\n" +
                "   public String delete(@PathVariable(\"id\") Integer id){\n" +
                "\n" +
                "      service.delete"+this.beanName+"(id);\n" +
                "      return \"delete "+this.originName+" by id :\" +id;\n" +
                "    }" + newLine);


        stringBuilder.append("}");


        writeToFile(this.beanName + "Controller.java", stringBuilder.toString());
        return stringBuilder.toString();
    }

    public String buildBeanFile() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("package " + packagePath + ";" + newLine); //写包路径

        //写默认的importer
        sb.append(
                "import io.swagger.annotations.ApiModel;" +
                        "import io.swagger.annotations.ApiModelProperty;" +
                        "import org.springframework.stereotype.Component;" +
                        "import javax.persistence.*;"
        );

        if (isEntity) {
            sb.append("@Entity" + newLine);
            sb.append("@Table(name = \"" + this.originName + "\")" + newLine);
        }

        sb.append("@Component" + newLine);
        sb.append("@ApiModel(value=" + nio + ",description=" + nio + ")" + newLine); //add swagger api inno

        sb.append("public class " + this.beanName + " {" + newLine);

        //add fields part
        for (Field f : this.fields) {
            if (isEntity) {
                if ("id".equalsIgnoreCase(f.name)) {
                    sb.append("@Id" + newLine + "@GeneratedValue(strategy = GenerationType.IDENTITY)" + newLine);
                } else {
                    sb.append("@Column()" + newLine);
                }
            }
            sb.append("@ApiModelProperty(value = " + nio + ")" + newLine);
            sb.append(f.access + blank + f.type + blank + f.name + ";" + newLine);
            sb.append(newLine);
        }

        sb.append(newLine);

        // add getter and setter
        for (Field f : this.fields) {
            String newName = f.name.substring(0, 1).toUpperCase() + f.name.substring(1); // uppercase first character
            // setter
            sb.append(
                    "public void " + "set" + newName + "(" + f.type + blank + f.name + "){" + newLine
                            + "this." + f.name + "=" + f.name + " ;" + newLine
                            + " }"
                            + newLine
            );

            //getter
            sb.append(
                    "public " + f.type + " get" + newName + "(){" + newLine
                            + "return " + "this." + f.name + " ;" + newLine
                            + " }"
                            + newLine
            );


        }
        sb.append("}");

        writeToFile(this.beanName + ".java", sb.toString());
        return sb.toString();
    }

    public String buildServiceFile() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String name = this.beanName + "Service";
        String fileName = name + ".java";

        stringBuilder.append("package " + this.packagePath + ";" + newLine);
        stringBuilder.append("public interface " +
                name +
                " {" + newLine);

        stringBuilder.append("" + this.beanName + " add" + this.beanName + "(" + this.beanName + " " + this.originName + ");" + newLine);
        stringBuilder.append("" + this.beanName + " get" + this.beanName + "(Integer id);" + newLine);
        stringBuilder.append("" + this.beanName + " update" + this.beanName + "(" + this.beanName + " " + this.originName + ");" + newLine);
        stringBuilder.append("void delete" + this.beanName + "(" + this.beanName + " " + this.originName + ");" + newLine);
        stringBuilder.append("void delete" + this.beanName + "(Integer id);" + newLine);

        stringBuilder.append("}");

        writeToFile(fileName, stringBuilder.toString());
        return stringBuilder.toString();
    }

    public String buildServiceImplFile() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String name = this.beanName + "ServiceImpl";
        String fileName = name + ".java";

        stringBuilder.append("package " + this.packagePath + ";" + newLine);
        stringBuilder.append("import org.springframework.beans.factory.annotation.Autowired;" + newLine);
        stringBuilder.append("import org.springframework.stereotype.Service;" + newLine + newLine);

        stringBuilder.append("@Service" + newLine);
        stringBuilder.append("public class " + name + " implements " + this.beanName + "Service" + " {" + newLine +
                "@Autowired" + newLine +
                this.beanName + "Repository repository;" + newLine);

        // add default methods:
        //  add
        stringBuilder.append("     @Override\n" +
                "    public " + this.beanName + " add" + this.beanName + "(" + this.beanName + " " + this.originName + ") {\n" +
                "        return repository.save(" + this.originName + ");\n" +
                "    }" + newLine);

        // get by id
        stringBuilder.append("    @Override\n" +
                "    public " + this.beanName + " get" + this.beanName + "(Integer id) {\n" +
                "        return repository.findById(id).get();\n" +
                "    }" + newLine);

        //update
        stringBuilder.append("    @Override\n" +
                "    public " + this.beanName + " update" + this.beanName + "(" + this.beanName + " " + this.originName + ") {\n" +
                "        return repository.save(" + this.originName + ");\n" +
                "    }" + newLine);

        //delete
        stringBuilder.append("    @Override\n" +
                "    public void delete" + this.beanName + "(" + this.beanName + " " + this.originName + ") {\n" +
                "       repository.delete(" + this.originName + ");\n" +
                "    }" + newLine);

        //delete by id
        stringBuilder.append("    @Override\n" +
                "    public void delete" + this.beanName + "(Integer id) {\n" +
                "        repository.deleteById(id);\n" +
                "    }" + newLine);


        stringBuilder.append("}");

        writeToFile(fileName, stringBuilder.toString());
        return stringBuilder.toString();
    }


    public void buildFiles() throws IOException {
        buildBeanFile();
        buildRepositoryFile();
        buildRepositoryHelpFile();
        buildRepositoryImplFile();
        buildServiceFile();
        buildServiceImplFile();
        buildControllerFile();
    }


    private void writeToFile(String fileName, String fileString) throws IOException {

        File folder = new File(this.filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, fileName);

        if (!file.exists()) {
            file.createNewFile();
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(fileString);
        }
    }


    private AutoJOPO isEntity(boolean isEntity) {
        this.isEntity = isEntity;
        return this;
    }

    private AutoJOPO addSwagger(boolean addSwagger) {
        this.addSwagger = addSwagger;
        return this;
    }

    private AutoJOPO addValidation(boolean addValidation) {

        this.addValidation = addValidation;
        return this;
    }

    private String getFilePath() {
        String filePath = "";
        try {
            filePath = new File("").getCanonicalPath() + //项目路径
                    "\\" + "src" + "\\" + "main" + "\\" + "java" + //源码文件夹
                    "\\" + this.packagePath.replace(".", "\\"); //包路径
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    private String getIdType() {
        for (Field field : this.fields) {

            if (field.name.equalsIgnoreCase("id")) {
                return field.type;
            }
        }
        return "";
    }


    class Field {
        String name;
        String access;
        String type;

        public Field(String access, String type, String name) {
            this.name = name;
            this.access = access;
            this.type = type;
        }

    }

    public AutoJOPO(String beanName, String packagePath) {
        this.beanName = beanName.substring(0, 1).toUpperCase() + beanName.substring(1);
        this.originName=beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
        this.packagePath = packagePath;
        this.filePath = getFilePath();
    }
}
