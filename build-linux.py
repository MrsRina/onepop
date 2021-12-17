class Compile:
	def __init__(self):
		self.run()

	def run(self):
		import os	
		os.system("./gradlew setupDevWorkspace --refresh-dependencies && ./gradlew build --console=rich")
		#os.system("./gradlew runClient --stop && ./gradlew clean build")

		import shutil
		try:
			shutil.copyfile("build/libs/onepop-2.0beta-all.jar", "/home/glauco/.minecraft/mods/1.12.2/onepop-2.0beta.jar")
			print("Copiadokkk")
		except:
			print("Ta sem o lib fodase")

		import sys
		sys.exit()

Compile()
