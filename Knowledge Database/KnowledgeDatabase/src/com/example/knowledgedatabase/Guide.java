package com.example.technicalsupportv2;

import java.util.ArrayList;

public class Guide {

	int guideID;
	int catID;
	ArrayList<Step> stepArray;
	String guideTitle;

	public Guide() {


	}

	public int getGuideID() {
		return guideID;
	}

	public int getCatID() {
		return catID;
	}

	public String getGuideTitle() {
		return guideTitle;
	}

	public void setGuideID(int gID) {
		this.guideID = gID;
	}

	public void setCatID(int cID) {
		this.catID = cID;
	}

	public void setGuideTitle(String title) {
		this.guideTitle = title;
	}

	public void setStepArray(ArrayList<Step> stepArray) {
		this.stepArray = stepArray;
	}

}
