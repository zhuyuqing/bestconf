package cn.ict.zyq.bestConf.COMT2;

import weka.core.Instances;

public abstract class UnlabledTrainingInstanceGen2 {

	public abstract Instances generateMore(int number, int existedNum, Instances header);
}
