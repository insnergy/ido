function encryptByAES(content) {
	var key = CryptoJS.enc.Hex.parse('4875383519512e01aff63dc3958edec5'),
		iv = CryptoJS.enc.Hex.parse('7a70dad21d08b97e54b49c9a006ed02e'),
		options = { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv };
	
	var encryptedContent = CryptoJS.AES.encrypt(content, key, options);
	return encryptedContent; 
}