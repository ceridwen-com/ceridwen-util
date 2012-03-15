package com.ceridwen.util.versioning;

public class LibraryIdentifier {
	protected String libraryName;
	protected String vendorId;
	
	public LibraryIdentifier(String vendorId, String libraryName) {
		this.libraryName = libraryName;
		this.vendorId = vendorId;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof LibraryIdentifier)) {
			return false;
		}
		
		if (this.libraryName == null) {
			if (((LibraryIdentifier)obj).libraryName != null) {
				return false;
			}
		} else { 
			if (!(this.libraryName.equals(((LibraryIdentifier)obj).libraryName))) {
				return false;
			}
		}
		
		if (this.vendorId == null) {
			if (((LibraryIdentifier)obj).vendorId != null) {
				return false;
			}
		} else { 
			if (!(this.vendorId.equals(((LibraryIdentifier)obj).vendorId))) {
				return false;
			}
		}

		return true;
	}
	
	public int hashCode() {
		return new String(vendorId + ":" + libraryName).hashCode();		
	}
	
	
}
