var basic_operators = 
[
	'*', 'COUNT(*)', 'COUNT(a)', 'DISTINCT()',
	'SUM(a)', 'MIN(a)', 'MAX(a)', 'AVG(a)',
	'AND ()', 'OR ()', 'BETWEEN a AND b'
];
	
var criteria_operators = 
[
	'<', '< \'\'', '< (SELECT  FROM  WHERE)',
	'<=', '<= \'\'', '<= (SELECT  FROM  WHERE)',
	'>', '> \'\'', '> (SELECT  FROM  WHERE)',
	'>=', '>= \'\'', '>= (SELECT  FROM  WHERE)',
	'=', '= \'\'', '= (SELECT  FROM  WHERE)',
	'!=', '!= \'\'', '!= (SELECT  FROM  WHERE)',
	'<>', '<> \'\'', '<> (SELECT  FROM  WHERE)',
	'LIKE', 'LIKE \'\'', 'LIKE \'%\'', 'LIKE \'%%\'',
	'LIKE (SELECT  FROM  WHERE)', 'IN (a, b)', 'IN (SELECT  FROM  WHERE)'
];
	
var keywords = 
[
	'SELECT', 'SELECT *', 'SELECT COUNT(*)', 'SELECT DISTINCT()',
	'COUNT(a)', 'SUM(a)', 'MIN(a)', 'MAX(a)', 'AVG(a)', 'AS',
	'FROM', 'WHERE', 'ORDER BY', 'GROUP BY', 'ASC', 'DESC',
	'HAVING', 'HAVING COUNT(*)', 'HAVING SUM(a)', 'HAVING MIN(a)',
	'HAVING MAX(a)', 'HAVING AVG(a)', 'AND ()', 'OR ()', 'BETWEEN a AND b',
	'JOIN <table_name> ON', 'RIGHT JOIN <table_name> ON', 'LEFT JOIN <table_name> ON'
];