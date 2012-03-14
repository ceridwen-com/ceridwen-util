package com.ceridwen.util.versioning;

public class LibraryIdentifier {
	protected String libraryName;
	protected String vendorId;
	
	public LibraryIdentifier(String vendorId, String libraryName) {
		this.libraryName = libraryName;
		this.vendorId = vendorId;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof LibraryIdentifier && obj != null) {
			if (this.libraryName.equals(((LibraryIdentifier)obj).libraryName) && this.vendorId.equals(((LibraryIdentifier)obj).vendorId)) {
				return true;
			}
		}			
		return false;
	}
	
	public int hashCode() {
		return new String(vendorId + ":" + libraryName).hashCode();		
	}
	
	
}
