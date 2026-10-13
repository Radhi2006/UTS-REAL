# UTS ADSE – Pengajuan Cuti Karyawan (Spring Boot + Camunda BPMN)

Aplikasi sederhana yang alur bisnisnya dimodelkan dengan **BPMN** (Camunda Modeler) lalu
dijalankan oleh **Camunda 7 Process Engine** di dalam aplikasi **Spring Boot** (Jakarta EE / Servlet).

## Teknologi

| Komponen | Versi |
|---|---|
| Java (JDK) | 21+ (diuji dengan JDK 23) |
| Spring Boot | 3.5.16 |
| Camunda Platform 7 (engine, REST, webapp) | 7.24.0 |
| Database | H2 (file `./data/camunda-db`) |
| UI | Thymeleaf |
| Build tool | Gradle (wrapper sudah ada) |
| Modeler | Camunda Modeler 5.x |

> Catatan: Camunda 7 belum mendukung Spring Boot 4, karena itu project memakai Spring Boot 3.5.

## Studi kasus: Pengajuan Cuti

![diagram](docs/pengajuan-cuti.png) <!-- ganti dengan screenshot dari Camunda Modeler -->

1. **Karyawan mengajukan cuti** (start event) – isi nama, jumlah hari, alasan.
2. **Validasi pengajuan** (service task → `ValidasiCutiDelegate`) – nama wajib diisi, jumlah hari 1–12.
3. **Gateway "Data valid?"**
   - tidak → **Catat cuti ditolak** → end *Cuti ditolak*
   - ya → **Persetujuan atasan** (user task, grup `camunda-admin`)
4. **Gateway "Disetujui?"**
   - ya → **Catat cuti disetujui** (`CutiDisetujuiDelegate`) → end *Cuti disetujui*
   - tidak → **Catat cuti ditolak** (`CutiDitolakDelegate`) → end *Cuti ditolak*

File BPMN: [`src/main/resources/bpmn/pengajuan-cuti.bpmn`](src/main/resources/bpmn/pengajuan-cuti.bpmn)
(bisa dibuka & diedit di Camunda Modeler). Proses uji instalasi: [`hello-world.bpmn`](src/main/resources/bpmn/hello-world.bpmn).

## Struktur project

```
src/main/java/com/radhi/utsreal
├── UtsRealApplication.java          # main Spring Boot
├── delegate/                        # kode Java yang dipanggil oleh service task BPMN
│   ├── HelloWorldDelegate.java
│   ├── ValidasiCutiDelegate.java
│   ├── CutiDisetujuiDelegate.java
│   └── CutiDitolakDelegate.java
├── service/CutiService.java         # memanggil RuntimeService / TaskService / HistoryService Camunda
└── web/CutiController.java          # endpoint web (/, /cuti, /task/{id}, /hello)
src/main/resources
├── application.properties           # konfigurasi datasource + user admin Camunda
├── bpmn/                            # model BPMN (otomatis di-deploy saat aplikasi start)
└── templates/index.html             # halaman UI
src/test/java/.../UtsRealApplicationTests.java   # tes 3 skenario proses
```

## Instalasi & konfigurasi

1. Install **JDK 21+** dan pastikan `java -version` jalan.
2. Download **Camunda Modeler** dari https://camunda.com/download/modeler/ (ekstrak, jalankan `Camunda Modeler.exe`).
3. Buka file `.bpmn` di folder `src/main/resources/bpmn` dengan Camunda Modeler untuk melihat/mengubah model.
4. Buka project ini di IntelliJ IDEA (Gradle akan mengunduh dependency otomatis).

## Menjalankan

```bash
./gradlew bootRun
```

| URL | Keterangan |
|---|---|
| http://localhost:8080/hello?nama=Radhi | **Hello World** – menjalankan proses `hello-world.bpmn` |
| http://localhost:8080/ | Aplikasi pengajuan cuti |
| http://localhost:8080/camunda/app/ | Camunda Cockpit / Tasklist / Admin (login `demo` / `demo`) |
| http://localhost:8080/engine-rest/process-definition | REST API Camunda |
| http://localhost:8080/h2-console | Console database H2 (JDBC URL `jdbc:h2:file:./data/camunda-db`, user `sa`) |

Menjalankan tes:

```bash
./gradlew test
```

## Skenario demo

| Input | Hasil yang diharapkan |
|---|---|
| Budi, 3 hari → atasan klik **Setujui** | status `DISETUJUI` |
| Sari, 2 hari → atasan klik **Tolak** | status `DITOLAK` (ditolak atasan) |
| Andi, 20 hari | langsung `DITOLAK` (melebihi jatah 12 hari), tidak masuk ke atasan |

Task "Persetujuan atasan" juga bisa diselesaikan lewat **Camunda Tasklist** (login `demo`), dan
jalannya setiap proses bisa dipantau di **Cockpit**.

## Checklist dokumentasi (untuk laporan UTS)

- [ ] Penjelasan 5W Spring Boot, BPM/BPMN, dan keterkaitannya (tulis dengan kata-kata sendiri)
- [ ] Screenshot instalasi JDK, IntelliJ, Camunda Modeler
- [ ] Screenshot hasil `/hello` (Hello World)
- [ ] Screenshot diagram BPMN di Camunda Modeler
- [ ] Screenshot form pengajuan, daftar persetujuan, dan riwayat
- [ ] Screenshot Cockpit (process instance) dan Tasklist
- [ ] Screenshot hasil `./gradlew test`
- [ ] Rekaman layar demo
- [ ] Push ke GitHub dan share ke rizalbroer@uinjkt.ac.id
