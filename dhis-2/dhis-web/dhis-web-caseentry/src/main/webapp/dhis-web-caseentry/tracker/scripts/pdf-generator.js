function pdfGenerator() {

    /*var reportImage;
    var doc = new jsPDF();
    html2canvas(document.body, {
        onrendered: function(canvas) {
            reportImage = canvas.toDataURL("image/jpeg");          
            doc.addImage(reportImage, 'JPEG', 15,40,180,180);            
            doc.save('lab-report.pdf');          
        }
    });   */
    
    var reportData = $('#anc_lab_report').html();
    
    console.log(reportData);
}

