function toggleMenu()
{
	var os = $("#mobilemenubutton").offset();
	if(os.left == 20)
	{
		$("#accessbar").animate({'margin-left':'210px'},500);
		$("#mobilemenubutton").animate({'left':'230px'},500);
		$("#pagemenu").animate({'left':'0px'},500);
	} else {
		$("#accessbar").animate({'margin-left':'0px'},500);
		$("#mobilemenubutton").animate({'left':'20px'},500);
		$("#pagemenu").animate({'left':'-210px'},500);
	}
}
