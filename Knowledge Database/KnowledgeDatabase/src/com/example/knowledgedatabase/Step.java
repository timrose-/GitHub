package com.example.knowledgedatabase;

import android.graphics.Bitmap;

public class Step {
	
	int stepsID;
	int stepNo;
	String stepTxt;
	Bitmap stepImg;
	
	public Step(int No, String Txt, Bitmap Img) {
		stepNo = No;
		stepTxt = Txt;
		stepImg = Img;
		
	}

	public int getStepsID() {
		return stepsID;
	}

	public int getStepNo() {
		return stepNo;
	}

	public String getStepTxt() {
		return stepTxt;
	}

	public Bitmap getStepImg() {
		return stepImg;
	}
	
	

}
