var BaseXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
"<project source=\"2.7.1\" version=\"1.0\">" +
"This file is intended to be loaded by Logisim (http://www.cburch.com/logisim/)." +
"<lib desc=\"#Wiring\" name=\"0\"/>" +
"<lib desc=\"#Gates\" name=\"1\"/>" +
"<lib desc=\"#Plexers\" name=\"2\"/>" +
"<lib desc=\"#Arithmetic\" name=\"3\"/>" +
  "<lib desc=\"#Memory\" name=\"4\"/>" +
 "<lib desc=\"#I/O\" name=\"5\"/>" +
 "<lib desc=\"#Base\" name=\"6\">" +
   "<tool name=\"Text Tool\">" +
     "<a name=\"text\" val=\"\"/>" +
     "<a name=\"font\" val=\"SansSerif plain 12\"/>" +
     "<a name=\"halign\" val=\"center\"/>" +
     "<a name=\"valign\" val=\"base\"/>" +
   "</tool>" +
 "</lib>" +
 "<main name=\"main\"/>" +
 "<options>" +
   "<a name=\"gateUndefined\" val=\"ignore\"/>" +
   "<a name=\"simlimit\" val=\"1000\"/>" +
   "<a name=\"simrand\" val=\"0\"/>" +
 "</options>" +
 "<mappings>" +
   "<tool lib=\"6\" map=\"Button2\" name=\"Menu Tool\"/>" +
   "<tool lib=\"6\" map=\"Button3\" name=\"Menu Tool\"/>" +
   "<tool lib=\"6\" map=\"Ctrl Button1\" name=\"Menu Tool\"/>" +
 "</mappings>" +
 "<toolbar>" +
   "<tool lib=\"6\" name=\"Poke Tool\"/>" +
   "<tool lib=\"6\" name=\"Edit Tool\"/>" +
   "<tool lib=\"6\" name=\"Text Tool\">" +
     "<a name=\"text\" val=\"\"/>" +
     "<a name=\"font\" val=\"SansSerif plain 12\"/>" +
     "<a name=\"halign\" val=\"center\"/>" +
     "<a name=\"valign\" val=\"base\"/>" +
   "</tool>" +
   "<sep/>" +
   "<tool lib=\"0\" name=\"Pin\">" +
     "<a name=\"tristate\" val=\"false\"/>" +
   "</tool>" +
   "<tool lib=\"0\" name=\"Pin\">" +
     "<a name=\"facing\" val=\"west\"/>" +
     "<a name=\"output\" val=\"true\"/>" +
     "<a name=\"labelloc\" val=\"east\"/>" +
   "</tool>" +
   "<tool lib=\"1\" name=\"NOT Gate\"/>" +
   "<tool lib=\"1\" name=\"AND Gate\"/>" +
   "<tool lib=\"1\" name=\"OR Gate\"/>" +
 "</toolbar>" +
 "<circuit name=\"main\">" +
   "<a name=\"circuit\" val=\"main\"/>" +
   "<a name=\"clabel\" val=\"\"/>" +
   "<a name=\"clabelup\" val=\"east\"/>" +
   "<a name=\"clabelfont\" val=\"SansSerif plain 12\"/>" +
  // "<wire from=\"(200,180)\" to=\"(230,180)\"/>

   "<comp lib=\"0\" loc=\"(550,240)\" name=\"Pin\">" +
     "<a name=\"facing\" val=\"west\"/>" +
     "<a name=\"output\" val=\"true\"/>" +
     "<a name=\"labelloc\" val=\"east\"/>" +
   "</comp>" +
   "<comp lib=\"0\" loc=\"(550,290)\" name=\"Pin\">" +
     "<a name=\"facing\" val=\"west\"/>" +
     "<a name=\"output\" val=\"true\"/>" +
     "<a name=\"labelloc\" val=\"east\"/>" +
   "</comp>" +
   "<comp lib=\"1\" loc=\"(210,120)\" name=\"NOT Gate\"/>" +
   "<comp lib=\"1\" loc=\"(490,190)\" name=\"AND Gate\"/>" +
   "<comp lib=\"0\" loc=\"(110,180)\" name=\"Pin\">" +
     "<a name=\"tristate\" val=\"false\"/>" +
   "</comp>" +
   "<comp lib=\"1\" loc=\"(490,140)\" name=\"AND Gate\"/>" +
   "<comp lib=\"1\" loc=\"(490,240)\" name=\"AND Gate\"/>" +
   "<comp lib=\"0\" loc=\"(110,120)\" name=\"Pin\">" +
     "<a name=\"tristate\" val=\"false\"/>" +
   "</comp>" +
   "<comp lib=\"1\" loc=\"(260,180)\" name=\"NOT Gate\"/>" +
   "<comp lib=\"0\" loc=\"(550,190)\" name=\"Pin\">" +
     "<a name=\"facing\" val=\"west\"/>" +
     "<a name=\"output\" val=\"true\"/>" +
     "<a name=\"labelloc\" val=\"east\"/>" +
   "</comp>" +
   "<comp lib=\"1\" loc=\"(490,290)\" name=\"AND Gate\"/>" +
   "<comp lib=\"0\" loc=\"(550,140)\" name=\"Pin\">" +
     "<a name=\"facing\" val=\"west\"/>" +
     "<a name=\"output\" val=\"true\"/>" +
     "<a name=\"labelloc\" val=\"east\"/>" +
   "</comp>" +
 "</circuit>" +
"</project>";
