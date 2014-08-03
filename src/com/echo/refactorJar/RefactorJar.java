package com.echo.refactorJar;

import java.io.*;   
import java.util.HashMap;
import java.util.Scanner;

import org.gjt.jclasslib.io.ClassFileWriter;   
import org.gjt.jclasslib.structures.CPInfo;   
import org.gjt.jclasslib.structures.ClassFile;   
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info;   
public class RefactorJar {   
	
	private static HashMap readMethodMap(String mapFileName){
		HashMap methodMap = new HashMap();
		String line;
		String originalName, newName;
		String[] tmp;
		
		try {
			Scanner scanner = new Scanner(new File(mapFileName));
			line = scanner.nextLine();
			while(line != null){
				line.trim();
				tmp = line.split("\\s+");
				originalName = tmp[0].trim();
				newName = tmp[1].trim();
				
				methodMap.put(originalName, newName);
				line = scanner.nextLine();
			}
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return methodMap;

	}
	
	private static void refactorClass(String classFileName, HashMap methodHashMap) throws Exception{
        FileInputStream fis = new FileInputStream(classFileName);   
        DataInput di = new DataInputStream(fis);   
        ClassFile cf = new ClassFile();   
        cf.read(di);   
        CPInfo[] infos = cf.getConstantPool();   
           
        int count = infos.length;   
        
        String originalName;
        String newName;
        
        for (int i = 0; i < count; i++) {   
            if (infos[i] != null) {   
            	if (infos[i].getTag() == CPInfo.CONSTANT_UTF8) {
	            	ConstantUtf8Info uInfo = (ConstantUtf8Info)infos[i];
	            	originalName = uInfo.getString();
	            	newName = (String) methodHashMap.get(originalName);
	            	if (newName != null) {
	            		uInfo.setString(newName);
	            		infos[i] = uInfo;
					}
					
				}
            	

            }   
        }   

        cf.setConstantPool(infos);   
        fis.close();   
        File f = new File(classFileName);   
        ClassFileWriter.writeToFile(f, cf);   
	}
	
	private static void refactor(String classesPath, HashMap methodHashMap){
		File file = new File(classesPath);
        String absolutePath;
		try {
			if (file.isDirectory()) {
				absolutePath = file.getAbsolutePath();
				String[] fileNames = file.list();
				for (int i = 0; i < fileNames.length; i++) {
					refactor(absolutePath + File.separator + fileNames[i], methodHashMap);
				}
			}else {
				refactorClass(classesPath, methodHashMap);
			}
		} catch (Exception e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public static void main(String[] args) throws Exception {   
    	if (args.length < 2) {
			System.out.println("usage: refactor dir methodMap");
		}
    	HashMap methodHashMap = readMethodMap(args[1]);
    	refactor(args[0], methodHashMap);

  
    }   
}  