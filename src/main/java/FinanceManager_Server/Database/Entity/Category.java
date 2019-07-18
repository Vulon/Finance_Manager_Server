package FinanceManager_Server.Database.Entity;

import FinanceManager_Server.Database.Entity.Database_pk.CategoryPK;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


@Entity
@Table(name = "category")
@IdClass(CategoryPK.class)
public class Category  implements Serializable, Action {
    private static final long serialVersionUID = -6265203350166782424L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @OneToOne
    private Category parent;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "commit_date")
    private Date commitDate;

    public Category() {
    }

    public Category(Long user_id, String color, String name, Integer icon_id, Category parent, Date commitDate) {
        this.user = user_id;
        this.color = color;
        this.name = name;
        this.icon_id = icon_id;
        this.parent = parent;
        this.commitDate = commitDate;
    }
    public Category(CategoryAction action, Category parent){
        this.user = action.getUser();
        this.color = action.getColor();
        this.name = action.getName();
        this.icon_id = action.getIcon_id();
        this.parent = parent;
        this.commitDate = action.getCommitDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(this.category, category.category) &&
                Objects.equals(user, category.user) &&
                Objects.equals(color, category.color) &&
                Objects.equals(name, category.name) &&
                Objects.equals(icon_id, category.icon_id) &&
                Objects.equals(parent, category.parent) &&
                Objects.equals(commitDate, category.commitDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, user, color, name, icon_id, parent, commitDate);
    }

    @Override
    public String getType() {
        return "category";
    }

    @Override
    public boolean isCreate() {
        return true;
    }

    @Override
    public void setCreate(boolean create) {

    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }
    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
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

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }
}

