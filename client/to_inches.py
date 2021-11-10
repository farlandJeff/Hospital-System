import subprocess

cmd = "ils /tempZone/home/public | cut -f4 -d ' '"
all_patients = subprocess.run(cmd, shell=True, stdout=subprocess.PIPE).stdout.decode('utf-8').splitlines()
print(all_patients)

for patient in all_patients:
    if(patient.find(':') == -1):
        cmd = f"imeta set -C {patient} height 68"
        subprocess.run(cmd, shell=True)
