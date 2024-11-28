可以使用以下命令查看当前生成密钥的 keyName

使用 `gpg --list-secret-keys` 查看已生成的证书

```shell
gpg --list-secret-keys --keyid-format LONG
```

它大概会给出如下返回

```text
[keyboxd]
---------
sec   ed25519/D921F05BB90099F5 2024-11-19 [SC] [expires: 2027-11-20]
      CB7FEA15093F43688789723AD921F05BB90099F5
uid                 [ultimate] 0B15C1520FA8C47EA69392A3982E4AC30AA3320A <truenine304520@gmail.com>
ssb   cv25519/5581D1D151A4F593 2024-11-19 [E] [expires: 2027-11-20]
```

其中 `sec` 的第二行就是密钥的 keyName
