package FinanceManager_Server.Database.Entity;


import FinanceManager_Server.Database.Entity.Database_pk.CategoryPK;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "category_action")
@JsonIgnoreProperties
@IdClass(CategoryPK.class)
public class CategoryAction implements Serializable, Action {

    private static final long serialVersionUID = -7163736650756578806L;
    @Column(name = "create")
    private boolean create;

    @Column(name = "commit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date commitDate;

    @Id
    @Column(name = "category")
    private Long category;

    @Id
    @Column(name = "user")
    private Long user;

    @Column(name = "color")
    private String color;

    @Column(name = "name")
    private String name;

    @Column(name = "icon_id")
    private Integer icon_id;

    @Column(name = "parent_id")
    private Long parent_id;

    private Long originalId;

    public static ArrayList<CategoryAction> toCategoryAction(Collection<Category> c){
        if (c == null){
            return new ArrayList<>();
        }
        ArrayList<CategoryAction> arrayList = new ArrayList<>(c.size());
        for(Category b : c){
            arrayList.add(new CategoryAction(true, b));
        }
        return arrayList;
    }

    public CategoryAction() {
    }

    public CategoryAction(boolean isCreate, Category category) {
        this.create = isCreate;

        this.commitDate = category.getCommitDate();
        this.category = category.getCategory();
        this.user = category.getUser();
        this.color = category.getColor();
        this.name = category.getName();
        this.icon_id = category.getIcon_id();
        if(category.getParent() == null){
            this.parent_id = -1l;
        }else{
            this.parent_id = category.getParent().getCategory();
        }
        this.originalId = category.getOriginalId();
    }

    @Override
    public Long getOriginalId() {
        return originalId;
    }

    public void setOriginalId(Long originalId) {
        this.originalId = originalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryAction that = (CategoryAction) o;
        return create == that.create &&
                Objects.equals(commitDate, that.commitDate) &&
                Objects.equals(category, that.category) &&
                Objects.equals(user, that.user) &&
                Objects.equals(color, that.color) &&
                Objects.equals(name, that.name) &&
                Objects.equals(icon_id, that.icon_id) &&
                Objects.equals(parent_id, that.parent_id);
    }


    @Override
    public String toString() {
        return "CategoryAction{" +
                "create=" + create +
                ", commitDate=" + commitDate +
                ", category=" + category +
                ", user=" + user +
                ", color='" + color + '\'' +
                ", name='" + name + '\'' +
                ", icon_id=" + icon_id +
                ", parent_id=" + parent_id +
                ", originalId=" + originalId +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(create, commitDate, category, user, color, name, icon_id, parent_id);
    }

    @Override
    public String getType() {
        return "category";
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
        originalId = category;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIcon_id() {
        return icon_id;
    }

    public void setIcon_id(Integer icon_id) {
        this.icon_id = icon_id;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }
}
