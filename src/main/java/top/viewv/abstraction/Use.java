package top.viewv.abstraction;


import java.util.Set;

public class Use extends BaseAbstraction{

	private Set<String> selectedColumn;
	//private Set<String> filterByColumn;
	private boolean isFromIndex;
	
	public Use(Codepoint codepoint)
	{
		super(codepoint);
	}
	

	public Set<String> getSelectedColumn() {
		return selectedColumn;
	}

	public void setSelectedColumn(Set<String> selectedColumn) {
		this.selectedColumn = selectedColumn;
	}
	
	public String toString()
	{
		return "Use:" + super.getCodepoint().toString() + "\nColumns:" + selectedColumn;
	}


	public void setIsFromIndex(boolean isFromIndex) {
		// TODO Auto-generated method stub
		this.isFromIndex = isFromIndex;
		
	}
	
	public boolean IsFromIndex() {
		// TODO Auto-generated method stub
		return isFromIndex;
		
	}
	/*
	public void setFilterByColumn(Set<String> filterByColumn) {
		this.filterByColumn = filterByColumn;
	}
	
	public Set<String> getFilterByColumn() {
		return this.filterByColumn;
	}
	*/

}
