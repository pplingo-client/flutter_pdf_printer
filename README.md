# description
A simple PDF printer for Android and iOS. 

Currently only works with PDFs, if other files are needed, feel free to create an issue and we'll 
look at adding it to the plugin.  

This library uses platform channels to call native printing functionality. As other platforms become
more available we will look at adding them to the platform channels. Native UI is used to print.   

# How to use 
Add
 ``` 
 flutter_pdf_printer: ^1.0.0+1
  ``` 
to your pubspec.yml
  
Then Call 
  
``` 
   FlutterPdfPrinter.print("path/to/file.pdf");
``` 
    
This will call the platform channel and will display the pdf file to be printed!

Check out the example for a sample of how to use this library!
