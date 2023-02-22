# Diff2XML - General purpose

![Version](https://img.shields.io/badge/version-1.0.0-success)
![License](https://img.shields.io/github/license/UnimibSoftEngCourse2022/progetto-questionari-1-dev-team)

Exercise project that generate a third xml containing the differences between two given xml. The differences will be shown like the "Git Commit" visualization.

For example:
```
<!--
  FIRST XML 
-->
<doc>
    <p>Lorem ipsum dolor sit amet <em>consectetur, adipisicing</em> elit.</p>
    <p>2th -- Lorem ipsum dolor sit amet <em>consectetur, adipisicing</em> elit.</p>
</doc>
```

```
<!--
  SECOND XML 
-->
<doc>
    <p>Lorem ipsum dolor sit amet <em>consectetur, adipisicing</em> elit.</p>
    <p>2th -- Lorem ipsum dolor sit amet <em>consectetur, adipisicing</em> elit.</p>
</doc>
```

The result will be:

```
<doc>
    <p>
        Lorem ipsum dolor sit amet 
        <del>
             consectetur, adipisicing elit
        </del>
        <ins>
             aspernatur, 
        </ins>
        <strong>
             <ins>
                   doloribus
             </ins>
        </strong>
        <ins>
             in libero
        </ins>
        .
    </p>
    <p>2th -- Lorem ipsum dolor sit amet <em>consectetur, adipisicing</em> elit.</p>
</doc>
```
