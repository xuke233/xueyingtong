/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.circuit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.UUID;

import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.analyze.model.Entry;
import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.analyze.model.TruthTable;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.Instance;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.instance.StdAttr;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.std.wiring.Pin;

public class Analyze {
	private static final int MAX_ITERATIONS = 100;
	
	private Analyze() { }
	
	//
	// getPinLabels
	//
	/** Returns a sorted map from Pin objects to String objects,
	 * listed in canonical order (top-down order, with ties
	 * broken left-right).
	 */
	//返回从pin对象到字符串对象的已排序映射，按规范顺序列出（自上而下的顺序，带断开的左-右关系）。
	public static SortedMap<Instance, String> getPinLabels(Circuit circuit) {
		Comparator<Instance> locOrder = new Comparator<Instance>() {
			public int compare(Instance ac, Instance bc) {
				Location a = ac.getLocation();
				Location b = bc.getLocation();
				if (a.getY() < b.getY()) return -1;
				if (a.getY() > b.getY()) return  1;
				if (a.getX() < b.getX()) return -1;
				if (a.getX() > b.getX()) return  1;
				return a.hashCode() - b.hashCode();
			}
		};
		SortedMap<Instance, String> ret = new TreeMap<Instance, String>(locOrder);

		// Put the pins into the TreeMap, with null labels将管脚放入Treemap，标签为空
		for (Instance pin : circuit.getAppearance().getPortOffsets(Direction.EAST).values()) {
			ret.put(pin, null);
		}
		
		// Process first the pins that the user has given labels.首先处理用户给定标签的管脚。
		ArrayList<Instance> pinList = new ArrayList<Instance>(ret.keySet());
		HashSet<String> labelsTaken = new HashSet<String>();
		for (Instance pin : pinList) {
			String label = pin.getAttributeSet().getValue(StdAttr.LABEL);
			label = toValidLabel(label);
			if (label != null) {
				if (labelsTaken.contains(label)) {
					int i = 2;
					while (labelsTaken.contains(label + i)) i++;
					label = label + i;
				}
				ret.put(pin, label);
				labelsTaken.add(label);
			}
		}
		
		// Now process the unlabeled pins.现在处理未标记的pin ，为未标记的管脚命名
		for (Instance pin : pinList) {
			if (ret.get(pin) != null) continue;
			
			String defaultList;
			if (Pin.FACTORY.isInputPin(pin)) {
				defaultList = Strings.get("defaultInputLabels");
				if (defaultList.indexOf(",") < 0) {
					defaultList = "a,b,c,d,e,f,g,h";
				}
			} else {
				defaultList = Strings.get("defaultOutputLabels");
				if (defaultList.indexOf(",") < 0) {
					defaultList = "x,y,z,u,v,w,s,t";
				}
			}
			
			String[] options = defaultList.split(",");
			String label = null;
			for (int i = 0; label == null && i < options.length; i++) {
				if (!labelsTaken.contains(options[i])) {
					label = options[i];
				}
			}
			if (label == null) {
				// This is an extreme measure that should never happen
				// if the default labels are defined properly and the
				// circuit doesn't exceed the maximum number of pins.
				//如果正确定义了默认标签，并且电路不超过插脚的最大数量，则这是一个绝对不应该发生的极端措施。
				int i = 1;
				do {
					i++;
					label = "x" + i;
				} while (labelsTaken.contains(label));
			}
			
			labelsTaken.add(label);
			ret.put(pin, label);
		}
		
		return ret;
	}
	
	private static String toValidLabel(String label) {
		if (label == null) return null;
		StringBuilder end = null;
		StringBuilder ret = new StringBuilder();
		boolean afterWhitespace = false;
		for (int i = 0; i < label.length(); i++) {
			char c = label.charAt(i);
			if (Character.isJavaIdentifierStart(c)) {
				if (afterWhitespace) {
					// capitalize words after the first one
					c = Character.toTitleCase(c);
					afterWhitespace = false;
				}
				ret.append(c);
			} else if (Character.isJavaIdentifierPart(c)) {
				// If we can't place it at the start, we'll dump it
				// onto the end.
				if (ret.length() > 0) {
					ret.append(c);
				} else {
					if (end == null) end = new StringBuilder();
					end.append(c);
				}
				afterWhitespace = false;
			} else if (Character.isWhitespace(c)) {
				afterWhitespace = true;
			} else {
				; // just ignore any other characters
			}
		}
		if (end != null && ret.length() > 0) ret.append(end.toString());
		if (ret.length() == 0) return null;
		return ret.toString();
	}
	
	//
	// computeExpression 计算表达式
	//
	/** Computes the expression corresponding to the given
	 * circuit, or raises ComputeException if difficulties
	 * arise.
	 */
	//计算与给定电路对应的表达式，或者在出现困难时引发计算异常。
	public static void computeExpression(AnalyzerModel model, Circuit circuit,
			Map<Instance, String> pinNames) throws AnalyzeException {
		ExpressionMap expressionMap = new ExpressionMap(circuit);
		
		ArrayList<String> inputNames = new ArrayList<>();
		ArrayList<String> outputNames = new ArrayList<>();
		ArrayList<Instance> outputPins = new ArrayList<>();
		for (Map.Entry<Instance, String> entry : pinNames.entrySet()) {
			Instance pin = entry.getKey();
			String label = entry.getValue();
			if (Pin.FACTORY.isInputPin(pin)) {
				expressionMap.currentCause = Instance.getComponentFor(pin);
				Expression e = Expressions.variable(label);
				expressionMap.put(pin.getLocation(), e);
				inputNames.add(label);
			} else {
				outputPins.add(pin);
				outputNames.add(label);
			}
		}
		
		propagateComponents(expressionMap, circuit.getNonWires());
		
		for (int iterations = 0; !expressionMap.dirtyPoints.isEmpty(); iterations++) {
			if (iterations > MAX_ITERATIONS) {
				throw new AnalyzeException.Circular();
			}
			
			propagateWires(expressionMap, new HashSet<Location>(expressionMap.dirtyPoints));

			HashSet<Component> dirtyComponents = getDirtyComponents(circuit, expressionMap.dirtyPoints);
			expressionMap.dirtyPoints.clear();
			propagateComponents(expressionMap, dirtyComponents);
			
			Expression expr = checkForCircularExpressions(expressionMap);
			if (expr != null) throw new AnalyzeException.Circular();
		}
		
		model.setVariables(inputNames, outputNames);
		for (int i = 0; i < outputPins.size(); i++) {
			Instance pin = outputPins.get(i);
			model.getOutputExpressions().setExpression(outputNames.get(i),
					expressionMap.get(pin.getLocation()));
		}
	}

	private static class ExpressionMap extends HashMap<Location,Expression> {
		private Circuit circuit;
		private Set<Location> dirtyPoints = new HashSet<>();
		private Map<Location, Component> causes = new HashMap<>();
		private Component currentCause = null;
		
		ExpressionMap(Circuit circuit) {
			this.circuit = circuit;
		}
		
		@Override
		public Expression put(Location point, Expression expression) {
			Expression ret = super.put(point, expression);
			if (currentCause != null) causes.put(point, currentCause);
			if (ret == null ? expression != null : !ret.equals(expression)) {
				dirtyPoints.add(point);
			}
			return ret;
		}
	}
		
	// propagates expressions down wires沿连线传播表达式
	private static void propagateWires(ExpressionMap expressionMap,
			HashSet<Location> pointsToProcess) throws AnalyzeException {
		expressionMap.currentCause = null;
		for (Location p : pointsToProcess) {
			Expression e = expressionMap.get(p);
			expressionMap.currentCause = expressionMap.causes.get(p);
			WireBundle bundle = expressionMap.circuit.wires.getWireBundle(p);
			if (e != null && bundle != null && bundle.points != null) {
				for (Location p2 : bundle.points) {
					if (p2.equals(p)) continue;
					Expression old = expressionMap.get(p2);
					if (old != null) {
						Component eCause = expressionMap.currentCause;
						Component oldCause = expressionMap.causes.get(p2);
						if (eCause != oldCause && !old.equals(e)) {
							throw new AnalyzeException.Conflict();
						}
					}
					expressionMap.put(p2, e);
				}
			}
		}
	}
		
	// computes outputs of affected components计算受影响组件的输出
	private static HashSet<Component> getDirtyComponents(Circuit circuit,
			Set<Location> pointsToProcess) throws AnalyzeException {
		HashSet<Component> dirtyComponents = new HashSet<>();
		for (Location point : pointsToProcess) {
			for (Component comp : circuit.getNonWires(point)) {
				dirtyComponents.add(comp);
			}
		}
		return dirtyComponents;
	}
		
	private static void propagateComponents(ExpressionMap expressionMap,
			Collection<Component> components) throws AnalyzeException {
		for (Component comp : components) {
			ExpressionComputer computer
				= (ExpressionComputer) comp.getFeature(ExpressionComputer.class);
			if (computer != null) {
				try {
					expressionMap.currentCause = comp;
					computer.computeExpression(expressionMap);
				} catch (UnsupportedOperationException e) {
					throw new AnalyzeException.CannotHandle(comp.getFactory().getDisplayName());
				}
			} else if (comp.getFactory() instanceof Pin) {
				; // pins are handled elsewhere 管脚在别处处理
			} else {
				// pins are handled elsewhere
				throw new AnalyzeException.CannotHandle(comp.getFactory().getDisplayName());
			}
		}
	}
	
	/** Checks whether any of the recently placed expressions in the
	 * expression map are self-referential; if so, return it. */
	//检查表达式映射中最近放置的任何表达式是否是自引用的；如果是，则返回它
	private static Expression checkForCircularExpressions(ExpressionMap expressionMap)
			throws AnalyzeException {
		for (Location point : expressionMap.dirtyPoints) {
			Expression expr = expressionMap.get(point);
			if (expr.isCircular()) return expr;
		}
		return null;
	}

	//
	// ComputeTable   计算真值表
	//
	/** Returns a truth table corresponding to the circuit. */
	//返回与电路对应的真值表。
	public static void computeTable(AnalyzerModel model, Project proj,
			Circuit circuit, Map<Instance, String> pinLabels) {   //接口 Map<K,V>  http://www.cjsdn.net/Doc/JDK50/java/util/Map.html
		ArrayList<Instance> inputPins = new ArrayList<>();
		ArrayList<String> inputNames = new ArrayList<>();
		ArrayList<Instance> outputPins = new ArrayList<>();
		ArrayList<String> outputNames = new ArrayList<>();
		for (Map.Entry<Instance, String> entry : pinLabels.entrySet()) {
			Instance pin = entry.getKey();
			if (Pin.FACTORY.isInputPin(pin)) {
				inputPins.add(pin);            //add(E o)将指定的元素追加到此列表的尾部。E为ArrayList的类型参数
				inputNames.add(entry.getValue());
			} else {
				outputPins.add(pin);
				outputNames.add(entry.getValue());
			}
		}
		
		int inputCount = inputPins.size();
		int rowCount = 1 << inputCount;    //1乘以2的inputCount次方
		Entry[][] columns = new Entry[outputPins.size()][rowCount];
		
		for (int i = 0; i < rowCount; i++) {
			CircuitState circuitState = new CircuitState(proj, circuit);        //CircuitState.java
			for (int j = 0; j < inputCount; j++) {
				Instance pin = inputPins.get(j);
				InstanceState pinState = circuitState.getInstanceState(pin);
				boolean value = TruthTable.isInputSet(i, j, inputCount);
				Pin.FACTORY.setValue(pinState, value ? Value.TRUE : Value.FALSE);
			}
			
			Propagator prop = circuitState.getPropagator();    //Propagator.java
			prop.propagate();
			/* TODO for the SimulatorPrototype class
			do {
				prop.step();
			} while (prop.isPending()); */
			// TODO: Search for circuit state
			
			if (prop.isOscillating()) {
				for (int j = 0; j < columns.length; j++) {
					columns[j][i] = Entry.OSCILLATE_ERROR;
				}
			} else {
				for (int j = 0; j < columns.length; j++) {
					Instance pin = outputPins.get(j);
					InstanceState pinState = circuitState.getInstanceState(pin);
					Entry out;
					Value outValue = Pin.FACTORY.getValue(pinState).get(0);
					if (outValue == Value.TRUE) out = Entry.ONE;
					else if (outValue == Value.FALSE) out = Entry.ZERO;
					else if (outValue == Value.ERROR) out = Entry.BUS_ERROR;
					else out = Entry.DONT_CARE;
					columns[j][i] = out;
				}
			}
		}
		model.setVariables(inputNames, outputNames);
		for (int i = 0; i < columns.length; i++) {
			model.getTruthTable().setOutputColumn(i, columns[i]);
		}

		//UUID uuid = UUID.randomUUID();
		//createFile(uuid+"TruthTable", "");
		for (Entry[] column : columns) {
			for (int i = 0; i < columns.length; i++) {
				/*
				try {
					writeFileContent(uuid + "TruthTable", column[i].toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				*/
				System.out.println("columns[" + i + "] = " + column[i].toString());
			}
		}

	}


	private static String path = "C:\\Users\\kxg\\MyFiles\\XinMiao\\";

	//文件路径+名称
	private static String filenameTemp;
	/**
	 * 创建文件
	 * @param fileName  文件名称
	 * @param filecontent   文件内容
	 * @return  是否创建成功，成功则返回true
	 */
	private static boolean createFile(String fileName, String filecontent){
		boolean bool = false;
		filenameTemp = path+fileName+".txt";//文件路径+名称+文件类型
		File file = new File(filenameTemp);
		try {
			//如果文件不存在，则创建新的文件
			if(!file.exists()){
				file.createNewFile();
				bool = true;
				System.out.println("success create file,the file is "+filenameTemp);
				//创建文件成功后，写入内容到文件里
				writeFileContent(filenameTemp, filecontent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bool;
	}

	/**
	 * 向文件中写入内容
	 * @param filepath 文件路径与名称
	 * @param newstr  写入的内容
	 * @throws IOException
	 */
	private static void writeFileContent(String filepath, String newstr) throws IOException{
		//boolean bool = false;
		String filein = newstr+"\r\n";//新写入的行，换行
		String temp;

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		FileOutputStream fos  = null;
		PrintWriter pw = null;
		try {
			File file = new File(filepath);//文件路径(包括文件名称)
			//将文件读入输入流
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			StringBuffer buffer = new StringBuffer();

			//文件原有内容
			for(int i=0;(temp =br.readLine())!=null;i++){
				buffer.append(temp);
				// 行与行之间的分隔符 相当于“\n”
				buffer = buffer.append(System.getProperty("line.separator"));
			}
			buffer.append(filein);

			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			pw.write(buffer.toString().toCharArray());
			pw.flush();
			//bool = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			//不要忘记关闭
			if (pw != null) {
				pw.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (br != null) {
				br.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
	}

	/**
	 * 删除文件
	 * @param fileName 文件名称
	 * @return
	 */
	public static boolean delFile(String fileName){
		Boolean bool = false;
		filenameTemp = path+fileName+".txt";
		File file  = new File(filenameTemp);
		try {
			if(file.exists()){
				file.delete();
				bool = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bool;
	}
}
