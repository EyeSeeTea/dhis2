# DHIS2-NRC project

Starting from a DHIS2 fork, this project aims to modify some core and modules features of DHIS2 2.23, specially from pivot tables webapp, in order to improve and extend some of its functionalities. The main objectives are to allow Indicators to be created in a DataSetAttribute basis, and providing a way and an interface to create calculated items in pivot tables by using other fields from the table.

It's splitted into 5 activities, one for each feature, and these activities are:

1. Introduce calculated items in pivot tables: Using DataElements, Indicators or any other idetifiable object from DHIS2 selected to be used in a pivot table, we provide a new interface to create "calculated items". These new objects will only be persisted accross the system in case the user includes that table into favorites. Otherwise the calculated item disappear as a temporary object. Each calculated item is attached to an specific table and cannot be used in any other pivot table.

1. Cumulative totals: Add options for creating cumulative totals (row and column) in pivot tables. These totals make the sum of the previous total plus their value,

1. New periods in pivot table: Add the periods weeks this year, months this year, bi-month this year and quarters this year in the API backend and in the front-end selection as well.

1. Add DataSet Attribute granularity to the Indicators creation: Split the data used to create an indicator so you can select not only the different CategoryComboOptions for a given DataSet, but also, into DataSet Attributes (CategoryComboOptions with Attribute checkbox selected). 

1. Conditional formatting in pivot tables: Add background selection on legends creation, so they can be used in pivot tables to represent the background color of the cell depending on the data value.

## Feedback

We’d love to hear your thoughts, improvements, new features or any suggestion in general. Just drop as a line at <a href="hello@eyeseetea.com">hello@eyeseetea.com</a> and let us know! If you prefer, you can also create a new issue on our GitHub repository. Note that you will have to register and be logged in to GitHub to create a new issue.

## License 

Modifications to DHIS2 code are covered by BSD license. Please respect the terms of that license. 


