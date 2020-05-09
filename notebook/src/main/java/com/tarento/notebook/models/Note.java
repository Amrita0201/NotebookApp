package com.tarento.notebook.models;

public class Note {
	private Long id;
	private String name;
	private String content;
	private Long bookId;
	private Boolean isStarred;
	private Boolean isActive;
	private Boolean isDeleted;
	private Long createdBy;
	private String creationDate;
	private Long updatedBy;
	private String updationDate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsStarred() {
		return isStarred;
	}
	public void setIsStarred(Boolean isStarred) {
		this.isStarred = isStarred;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public Long getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getUpdationDate() {
		return updationDate;
	}
	public void setUpdationDate(String updationDate) {
		this.updationDate = updationDate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Long getBookId() {
		return bookId;
	}
	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Note other = (Note) obj;
		if (this.getBookId() == null) {
			if (other.getBookId() != null)
				return false;
		} else if (!this.getBookId().equals(other.getBookId()))
			return false;
		return true;
	}
	
	
	
}
