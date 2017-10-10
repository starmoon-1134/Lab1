package com.graphviz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;


public class GraphViz {
	private String runPath = "";// ͼƬ+�ű����·��
	private String dotPath = "";// dot.exe��ִ��·��
	private String runOrder = "";// ����dot.exe�Ĳ���
	private String dotCodeFile = "graph.dot";// �ű��ļ���
	private String resultGra = "graph";// ����ͼƬ�ļ���
	private StringBuilder graph = new StringBuilder();// �ű��ļ�������
	public static final String[] color = {"red", "chartreuse", "blue", "gold",
			"darkgoldenrod", "fuchsia", "hotpink", "cadetblue", "green", "cyan",
			"yellow"};// ��ѡ������ɫ

	Runtime runtime = Runtime.getRuntime();

	public void run() {
		// �ű�����������д���ű��ļ���
		writeGraphToFile(graph.toString(), runPath);
		// ����dot.exe�����������
		creatOrder();
		Process tmpProcess = null;
		try {
			tmpProcess = runtime.exec(runOrder);// ����dot.exe����ͼƬ
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			tmpProcess.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void creatOrder() {
		// ��������ʱ����
		runOrder += dotPath + " ";
		runOrder += runPath;
		runOrder += "\\" + dotCodeFile + " ";
		runOrder += "-T jpg ";
		runOrder += "-o ";
		runOrder += runPath;
		runOrder += "\\" + resultGra + ".jpg";
		// System.out.println(runOrder);
	}

	public void writeGraphToFile(String dotcode, String filename) {
		// �ű�����������д���ű��ļ���
		try {
			File file = new File(filename + "\\" + dotCodeFile);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(dotcode.getBytes());
			fos.close();
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public GraphViz(String dotPath) {
		// ���캯��
		File f = new File("");
		try {
			this.runPath = f.getCanonicalPath();// ��ȡ��ǰ��Ŀ�ĸ�Ŀ¼
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		this.dotPath = dotPath;
	}

	public void add(String line) {
		graph.append("\t" + line);
	}

	public void addln(String line) {
		graph.append("\t" + line + "\n");
	}

	public void addln(String line, int label) {
		graph.append(
				"\t" + line + "[label=" + String.valueOf(label) + "]\n");
	}

	public void addln() {
		graph.append('\n');
	}

	public void start_graph() {
		graph.append("digraph G {\n");
	}

	public void setColorForPath(Stack<String> path_Stack, String color,
			String dotName) {
		
		// ������ǵ�ÿ���߼���Set��
		Set<String> path_Set = new HashSet<String>();
		Stack<String> tmpStack = (Stack<String>) path_Stack.clone();// ����ǵ�·�����㣩
		String str1 = "";
		String str2 = tmpStack.peek();
		tmpStack.pop();
		while (!tmpStack.isEmpty()) {
			str1 = tmpStack.peek();
			tmpStack.pop();
			path_Set.add("\"" + str1 + "\"->\"" + str2 + "\"");
			str2 = str1;
		}

		// ���Ŀ��ű��ļ������ڣ���ԭʼ�ű��ļ�����
		String targetFile = runPath + "\\" + dotName;
		File outpuFile = new File(targetFile);
		if (!outpuFile.exists()) {
			try {
				Files.copy(new File(runPath + "\\graph.dot").toPath(),
						outpuFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int pos = 0;
		String sourceFileName = runPath + "\\" + dotName;
		String tmp_str = "";
		File sourceFile = new File(sourceFileName);
		FileReader souceFileReader = null;

		//prepare readStream
		// ��Ŀ��ű��ļ���dotName��
		try {
			souceFileReader = new FileReader(sourceFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader sourceBufferedReader = new BufferedReader(souceFileReader);
		
		//prepare writeStream
		// �½���ʱ�ű��ļ��������޸Ľ��
		outpuFile = new File(runPath + "\\tmp.dot");
		try {
			outpuFile.createNewFile();
		} catch (IOException e2) {
			// TODO �Զ����ɵ� catch ��
			e2.printStackTrace();
		}
		FileOutputStream fout=null;
		OutputStreamWriter outWriter=null;
		try {
			fout=new FileOutputStream(outpuFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		 try {
			outWriter = new OutputStreamWriter(fout, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}  
         BufferedWriter bufWrite = new BufferedWriter(outWriter); 

         //add Head
         try {
			bufWrite.write("digraph G {\n");
		} catch (IOException e1) {
			// TODO �Զ����ɵ� catch ��
			e1.printStackTrace();
		}
         
		// ���ж�ȡԴ�ű��ļ�������ָ��·���ϵı߽��б��
		try {
			while((tmp_str = sourceBufferedReader.readLine())!=null) {
				if ((pos = tmp_str.indexOf("[")) < 0) {// �ų��ű��ļ���ͷβ+����
					continue;
				}
				String edge = tmp_str.substring(tmp_str.indexOf("\t") + 1, pos);
				if (path_Set.contains(edge)) {// ��ǰ�ı��ڴ���ǵ�·����
					StringBuffer tmpBuffer = new StringBuffer(tmp_str);
					pos = tmp_str.indexOf("]");
					if (tmp_str.indexOf("color") >= 0) {// �Ѿ�������ĳ��·���У���Ϊ����
						tmpBuffer.insert(pos,
								" style=tapered penwidth=4 arrowtail=normal");
					} else {
						tmpBuffer.insert(pos, " color=" + color);
					}
					tmp_str = tmpBuffer.toString();
				}
				tmp_str += "\n";
				// �޸ĺ�д����ʱ�ű��ļ���
				bufWrite.write(tmp_str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// add tail && close stream
		 try {
			bufWrite.write("}");
			bufWrite.close();
			fout.close();
			sourceBufferedReader.close();
			souceFileReader.close();
			sourceFile.delete();// ɾ��Դ�ű��ļ�
			} catch (IOException e1) {
				// TODO �Զ����ɵ� catch ��
				e1.printStackTrace();
			}
		outpuFile.renameTo(sourceFile);// ����ʱ�ű��ļ�������βԴ�ű��ļ������´�ʹ��
	}

	public void end_graph() {
		graph.append("}");
	}

	public void clearTmpDotFile(String dotName) {
		// �����ʱ�ű��ļ�
		String fileNameString = runPath + "\\" + dotName;
		File tmpFile = new File(fileNameString);
		if (tmpFile.exists()) {
			tmpFile.delete();
		}
	}

	public void runAfterSetColor(String newJpgName, String dotName) {
		 //create new Jpg file
		resultGra = newJpgName;
		dotCodeFile = dotName;
		Process tmpProcess = null;
		creatOrder();
		try {
			tmpProcess = runtime.exec(runOrder);

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			tmpProcess.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
