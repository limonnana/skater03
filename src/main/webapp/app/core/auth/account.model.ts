export class Account {
  constructor(
    public activated: boolean,
    public authorities: string[],
    public email: string,
    public firstName: string | null,
    public langKey: string,
    public lastName: string | null,
    public login: string,
    public imageUrl: string | null,
    public picture: string | null | undefined,
    public profilePicture: string | null,
    public country: string | null,
    public phone: string | null
  ) {}
}
