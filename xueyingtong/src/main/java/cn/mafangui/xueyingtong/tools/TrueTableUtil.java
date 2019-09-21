package cn.mafangui.xueyingtong.tools;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.gui.menu.ProjectCircuitActions;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.proj.ProjectActions;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class TrueTableUtil {
	public static int getTrueTable(File file,String path){
		Project proj;
		proj = ProjectActions.doOpenT(null,null, file);
		Circuit cur = proj == null ? null : proj.getCurrentCircuit();
		int score = 0;
		try{
			score = ProjectCircuitActions.doAnalyzeT(proj, cur,path);
		}catch (Exception e){
			System.out.println(e.getMessage());
			score = -1;
		}
		return score;
	}
}
