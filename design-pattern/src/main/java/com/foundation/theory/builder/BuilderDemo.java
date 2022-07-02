package com.foundation.theory.builder;

/**
 * @author : jacksonz
 * @date : 2021/10/11 13:42
 */
public class BuilderDemo {

    public static void main(String[] args) {
        Student studentInfo = Student.builder()
                .setName("test1")
                .setAge(12)
                .setHobby("pu gai")
                .build();
        System.out.println(studentInfo);
    }
}

class Student {
    private String name;

    private Integer age;

    private String hobby;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", hobby='" + hobby + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Student.Builder();
    }

    public static class Builder {
        private String name;

        private Integer age;

        private String hobby;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAge(Integer age) {
            this.age = age;
            return this;
        }

        public Builder setHobby(String hobby) {
            this.hobby = hobby;
            return this;
        }

        public Student build() {
            Student student = new Student();
            student.setName(this.name);
            student.setAge(this.age);
            student.setHobby(this.hobby);
            return student;
        }
    }
}
