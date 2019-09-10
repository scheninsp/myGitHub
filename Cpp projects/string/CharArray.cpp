static long myfunc_str2int ( char* strLine){
int pos = 0;
long num = 0;
while ( strLine[pos] >= '0' && strLine[pos]<='9' && pos<5) {
num = 10* num + int( strLine[pos] - '0';
pos++;
}
if( num>65535L ){ return -1;}
else { return num; } 
}

static bool readStrings(string filepath){
FILE* fp = fopen( filepath.c_str(), "r" );

char strBuf[512] = {0};
fscanf( fp, "%[^\n]%*c", strBuf);

int nCases = myfunc_str2int( strBuf );
strArray = (char**) malloc ( nCases * sizeof( char* ) ); 

for ( int i=0; i<nCases; i++ ){
fscanf ( fp, "%[^\n]%*c", strBuf);
strArray[i] = (char*) malloc ( (strlen( strBuf)+1) * sizeof( char ) ); 
strcpy( strArray[i], strBuf);
}
}