package cn.edu.thu.thss.tsmart.da.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DiffGenerator1 {
	
	public static String OUTPUT_SCRIPT = "gen_diff.sh";
	public static String DIFF_FILE_NAME = "classes_modified";
	
	public static void main(String[] args) {
		try {
			
			File scriptfile = new File(Constants.DEFECTS4J_ROOT + "/" + OUTPUT_SCRIPT);
			if(!scriptfile.exists()){
				scriptfile.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(Constants.DEFECTS4J_ROOT + "/" + OUTPUT_SCRIPT)));
			writer.write("export PATH=$PATH:" + Constants.DEFECTS4J_ROOT + "/framework/bin");
			writer.newLine();
			
			genScriptForProgram(Constants.COMMONS_LANG_KEY, Constants.LANG_VER_NUM, writer);
			genScriptForProgram(Constants.COMMONS_MATH_KEY, Constants.MATH_VER_NUM, writer);
			genScriptForProgram(Constants.CLOSURE_COMPILER_KEY, Constants.CLOSURE_VER_NUM, writer);
			genScriptForProgram(Constants.JODA_TIME_KEY, Constants.TIME_VER_NUM, writer);
			genScriptForProgram(Constants.JFREE_CHART_KEY, Constants.CHART_VER_NUM, writer);
			writer.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	// all reps should be initialized before running this script,
	// so we don't check whether all working directories exist or not
	private static void genScriptForProgram(String programKey, int programVerNum, BufferedWriter scriptWriter) throws IOException{
		for(int i = 1; i <= programVerNum; i ++){
			//buggy version
			String workingDirBuggy = Constants.DEFECTS4J_ROOT + "/" + Constants.CHECKOUT_ROOT + "/" 
					+ programKey + "/v" + i + "/" + Constants.BUGGY;
			scriptWriter.write("cd " + workingDirBuggy);
			scriptWriter.newLine();
			scriptWriter.write(
					"defects4j export -p classes.modified -o " + workingDirBuggy + "/" + DIFF_FILE_NAME);
			scriptWriter.newLine();
		}
	}
}
