//记录output元件的坐标
var outPoint = new Array();
//xml字符串转换为logisimFile
function XMLToLogisimFile(xml){
  xmlDoc = readXml(xml);
  if(xmlDoc == null) return;
  var wires = searchWire(xmlDoc);
  var cellInfo = getAllCell(xmlDoc, wires);

  logisim = readXml(BaseXml);
  if(logisim == null) return;
  var circuit = logisim.getElementsByTagName("circuit")[0];
  var comp = circuit.getElementsByTagName('comp');
  var cellNames = new Array();

  //写wire节点
  for(var i=0; i<cellInfo.length; i++) {
    switch (cellInfo[i][0]) {
      case 'wire':
        insertWire(logisim, circuit, cellInfo[i][1], cellInfo[i][2]);
        break;
      default:cellNames.push(cellInfo[i][0]);
    }
  }

  //修改元件坐标
  for(var i=0; i<cellNames.length; i++) {
    var name = cellNames[i];
    var list = getCell(comp, name);
    if(list.length == 0) continue;
    var index = 0;
    for(var j=0; j<cellInfo.length; j++) {
      if(cellInfo[j][0] == name){
        list[index].setAttribute("loc", "(" + cellInfo[j][1][0] + "," + cellInfo[j][1][1] +")");
        index++;
      }
    }
  }
  return xmlToString(logisim);
}
//xml DOM 转字符串
function xmlToString(xmlDoc) {
   //IE浏览器
  if(document.all) {
      return xmlDoc.xml;
  }else {
    //其他浏览器
    return (new XMLSerializer()).serializeToString(xmlDoc);
  }
}
//xml字符串 转 xml DOM
function readXml(xml){
  //Internet Explorer
   try {
     xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
     xmlDoc.async="false";
     xmlDoc.loadXML(xml);
     return(xmlDoc);
   }
   catch(e) {
     //Firefox, Mozilla, Opera, etc.
     try {
       parser=new DOMParser();
       xmlDoc=parser.parseFromString(xml,"text/xml");
       return(xmlDoc);
     }
     catch(e) {}
   }
   return(null);
}
//获取所有元件信息
function getAllCell(xmlDoc, wireArray){
  //储存所有元件及信息
  var cellInfo = new Array();
  //获取所有插入类线条
  var insertWires = searchInsertWire(wireArray);
  var point = [0, 0];
  var temp;
  var cellName;
  //遍历所有线条
  for(var i=0; i<wireArray.length; i++) {
    //获取线条插点、转折点和终点
    temp = getWirePoint(xmlDoc, wireArray[i], insertWires);
    var parent = xmlDoc.getElementById(wireArray[i].getAttribute('source')).getAttribute('parent');
    //判断线条的源头是否是线条
    if(parent != 1) {
      cellName =xmlDoc.getElementById(parent).getAttribute('value');
      //根据线条源点坐标计算元件坐标
      var tempLocation = getWireSourcePoint(xmlDoc, wireArray[i]);
      var xLocation = parseInt(xmlDoc.getElementById(parent).getElementsByTagName('mxGeometry')[0].getAttribute('x')) + getCellWidth(cellName);
      switch (cellName) {
        case 'Pin1':
        case 'Pin2':
          cellName = 'Pin';
          break;
        case 'NOT1':
        case 'NOT2':
          cellName = 'NOT Gate';
          break;
        case 'A1':
        case 'A2':
        case 'A3':
        case 'A4':
          cellName = 'AND Gate';
          break;
        case 'OUT1':
        case 'OUT2':
        case 'OUT3':
        case 'OUT4':
          cellName = 'output';
          break;
        default:;
      }
      //测试数据
      cellInfo.push([cellName, tempLocation, [0, 0]]);
      cellInfo.push(['wire', tempLocation, temp[0]]);
    }
    for(var j=0; j<temp.length-1; j++) {
      cellInfo.push(['wire', temp[j], temp[j+1]]);
    }
  }
  //添加output元件信息
  for(var i=0; i<outPoint.length; i++) {
    cellInfo.push(['output', outPoint[i], [0, 0]]);
  }
  return cellInfo;
}

//查找线条node
function searchWire(xmlDoc){
  var wires = xmlDoc.getElementsByTagName("mxCell");
  var wireArray = new Array();

  for(var i=0; i<wires.length; i++){
    edge = wires[i].getAttribute("edge");
    if(edge == 1) wireArray.push(wires[i]);
  }
  return wireArray;
}
//查找插入类线条
function searchInsertWire(wires) {
  var insertWire = new Array();
  var temp;
  for(var i=0; i<wires.length; i++) {
    temp = wires[i].getElementsByTagName("mxPoint");
    for(var j=0; j<temp.length; j++) {
      if(temp[j].getAttribute("as") != null) {
        insertWire.push(wires[i]);
        break;
      }
    }
  }
  return insertWire;
}
//计算一条线条转折点和终点
function getWireLocation(xmlDoc, mxCell){
  //测试数据用
  var pp = 'wire';
  var location = new Array();
  //用于计算的中间数组变量
  var tempLocation = [0, 0];
  //获取中间拐点
  var mxPoint = mxCell.getElementsByTagName("mxPoint");
  for(var i=0; i<mxPoint.length; i++) location.push([parseInt(mxPoint[i].getAttribute("x")), toTen(mxPoint[i].getAttribute("y"))]);

  //如果线条的终点是元件，就需要计算最后一个拐点和终点
  var target = xmlDoc.getElementById(mxCell.getAttribute("target"));
  var parentEdge = target.getAttribute('edge');
  if(parentEdge == null) {
    //获取终点
    var targetPoint;
    var parentValue = 'Wire';
    var targetParent = xmlDoc.getElementById(target.getAttribute('parent'));
    targetPoint =targetParent.getElementsByTagName("mxGeometry");
    tempLocation[0] = parseInt(targetPoint[0].getAttribute("x"));
    tempLocation[1] = toTen(targetPoint[0].getAttribute("y"));
    parentValue = targetParent.getAttribute("value");
    //测试数据；
    pp = targetParent.getAttribute('value');
    targetPoint = target.getElementsByTagName("mxPoint");
    //+ parseInt(targetPoint[0].getAttribute("x"))
    tempLocation[0] = tempLocation[0] ;
    tempLocation[1] = tempLocation[1] + toTen(targetPoint[0].getAttribute("y"));
    if(location.length != 0) {
      //最后一个拐点计算
      var index = location.length - 1;
      var shortTime = [location[index][0], 0];
      shortTime[1] = tempLocation[1];
      location.push(shortTime);
    }
    //如果终点是output，记录下其坐标
    if(parentValue.indexOf('OUT') != -1) outPoint.push(tempLocation);
    //记录终点坐标
    location.push(tempLocation);
  }
  return location;
}
//得到一条线的插点、转折点和终点,不包括起点
function getWirePoint(xmlDoc, mxCell, insertWire) {
  //存储一条线的插点、转折点和终点
  var wirePoint = new Array();
  //计算一条线条转折点和终点
  var location = getWireLocation(xmlDoc, mxCell);
  //得到一条线的所有插入点；
  var points = isInsert(mxCell, insertWire);
  for(var i=0; i<location.length; i++) {
    for(var j=0; j<points.length; j++) {
      if(location[i][0] == points[j][0]) {
        if(points[j][1] < location[i][1]) {
          wirePoint.push(points[j]);
        }
      }else {
        if(points[j][1] == location[i][1]) {
          if(points[j][0] < location[i][0]) {
            wirePoint.push(points[j]);
          }
        }
      }
    }
    wirePoint.push(location[i]);
  }
  return wirePoint;
}
//获取一条线的起点
function getWireSourcePoint(xmlDoc, mxCell) {
  //测试用数据,表示起点名字
  var pp = 'wire';
  //遇到问题——插入类线条的源不需要再求相对坐标啦，求线条的终也一样——待完成
  var tempLocation = [0, 0];
  var source = xmlDoc.getElementById(mxCell.getAttribute("source"));
  var sourcePoint;
  var parentEdge = source.getAttribute("parent");
  if(parentEdge != 1){
    var sourceParent = xmlDoc.getElementById(source.getAttribute("parent"));
    //测试数据
    pp = sourceParent.getAttribute('value');
    sourcePoint =sourceParent.getElementsByTagName("mxGeometry");
    tempLocation[0] = parseInt(sourcePoint[0].getAttribute("x"));
    tempLocation[1] = toTen(sourcePoint[0].getAttribute("y"));
    sourcePoint = source.getElementsByTagName("mxPoint");
    if(sourcePoint.length != 0) {
      //其他原件
      // + parseInt(sourcePoint[0].getAttribute("x"))
      //var pWidth = getCellWidth(sourceParent.getAttribute('value'));
      tempLocation[0] = tempLocation[0] + parseInt(getCellWidth(sourceParent.getAttribute('value')));
      tempLocation[1] = tempLocation[1] + toTen(sourcePoint[0].getAttribute("y"));
    }else {
      //Pin原件的特殊情况，没有mxPoint标签
      var pWidth = sourceParent.getElementsByTagName('mxGeometry')[0].getAttribute('width');
      var pHeight = sourceParent.getElementsByTagName('mxGeometry')[0].getAttribute('height');
      sourcePoint = source.getElementsByTagName("mxGeometry");
      tempLocation[0] =tempLocation[0] + parseInt(pWidth);
      tempLocation[1] = tempLocation[1] + toTen(parseInt(pHeight)/2);
    }
  }else {
    sourcePoint = source.getElementsByTagName('mxPoint');
    //直接找出线条的源点
    for(var i=0; i<sourcePoint.length; i++) {
      if(sourcePoint[i].getAttribute('as') == 'sourcePoint') {
        tempLocation = [parseInt(sourcePoint[i].getAttribute('x')), toTen(sourcePoint[i].getAttribute('y'))];
        break;
      }
    }
  }
  return tempLocation;
}
//给一个线条中查找它的插入点
function isInsert(wire, insertWire) {
  var insertPoint = new Array();
  //所求线条的id
  var id = wire.getAttribute('id');
  //存放插入类线条的source和target
  var tempId = [0, 0];
  var mxPoint;
  for(var i=0; i<insertWire.length; i++) {
    tempId[0] = insertWire[i].getAttribute('source');
    if(id == tempId[0]) {
      mxPoint = insertWire[i].getElementsByTagName('mxGeometry')[0].getElementsByTagName('mxPoint');
      //寻找源点类插点坐标
      for(var j=0; j<mxPoint.length; j++) {
        if(mxPoint[j].getAttribute('as') == 'sourcePoint')
          insertPoint.push([parseInt(mxPoint[j].getAttribute('x')),toTen(mxPoint[j].getAttribute('y'))]);
      }
      //
    }else {
      tempId[1] = insertWire[i].getAttribute('target');
      if(id == tempId[1]) {
        mxPoint = insertWire[i].getElementsByTagName('mxGeometry')[0].getElementsByTagName('mxPoint');
        //寻找终点类插点坐标
        for(var j=0; j<mxPoint.length; j++) {
          if(mxPoint[j].getAttribute('as') == 'targetPoint')
            insertPoint.push([parseInt(mxPoint[j].getAttribute('x')), toTen(mxPoint[j].getAttribute('y'))]);
        }
        //
      }
    }
  }
  return insertPoint;
}
//获取元件宽度
function getCellWidth(cellName) {
  var temp;
  switch (cellName) {
    case 'NOT1':
    case 'NOT2':
      temp = 30;
      break;
    case 'A1':
    case 'A2':
    case 'A3':
    case 'A4':
      temp = 50;
      break;
    default:
      temp = 0;
  }
  return temp;
}
//取整十函数
function toTen(numString){
  var num = parseInt(numString);
  if(num%10 != 0) {
    num = num + 10;
    num = parseInt(num/10);
    num = num*10;
  }
  return num;
}
//为创建wire节点做准备
//获取原件Node
function getCell(comp, cellName) {
  var list = new Array();
  var temp = cellName;
  if(cellName == 'output') temp = 'Pin';
  for(var i=0; i<comp.length; i++){
    if(comp[i].getAttribute('name') == temp) list.push(comp[i]);
  }
  if(cellName == 'output' || cellName == 'Pin') {
    var output = new Array();
    var input = new Array();
    var m = 0;
    for(var i=0; i<list.length; i++) {
      m = 0;
      var aList = list[i].getElementsByTagName('a');
      for(var j=0; j<aList.length; j++) {
        if(aList[j].getAttribute('name') == 'output') {
          output.push(list[i]);
          m = 1;
          break;
        }
      }
      if(m == 0) input.push(list[i]);
    }
    if(cellName == 'output') list = output;
    else list = input;
  }
  return list;
}
//创建wire节点
function insertWire(logisim, circuit, from, to){
  var wire = logisim.createElement('wire');
  wire.setAttribute("from", "(" + from[0] + "," + from[1] + ")");
  wire.setAttribute("to", "(" + to[0] + "," + to[1] + ")");
  circuit.appendChild(wire);
}
